package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.*;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.util.ObjectId;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.*;
import net.ion.talk.bean.Const;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:44
 * To change this template use File | Settings | File Templates.
 */
public class TalkMessageHandler implements CDDHandler {

    private final NewClient nc;

    public TalkMessageHandler() {
        this.nc = NewClient.create(ClientConfig.newBuilder().setRequestTimeoutInMs(3000).setMaxRequestRetry(3).setMaximumConnectionsPerHost(5).build());
    }

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/messages/{messageId}";
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CacheEntryRemovedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {
        return null;
    }

    @Override
    public TransactionJob<Void> modified(Map<String, String> resolveMap, final CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String roomId = resolveMap.get(Const.Room.RoomId);
        final String messageId = resolveMap.get(Const.Message.MessageId);
        final PropertyValue receivers = event.getValue().get(PropertyId.fromIdString(Const.Message.Receivers));


        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                AtomicMap<PropertyId, PropertyValue> pmap = event.getValue() ;
                if(pmap.get(PropertyId.fromIdString(Const.Message.Filter)) != null)
                    return null;

                Iterator<String> botIter = wsession.pathBy("/rooms/" + roomId + "/members").childrenNames().iterator();

                while(botIter.hasNext()){
                    String botId = botIter.next();
                    if(receivers!=null && !receivers.asSet().contains(botId))
                        continue;

                    if(wsession.exists("/bots/"+botId) && !wsession.pathBy("/bots/"+botId).ref("bot").property(Const.Bot.isSyncBot).equals(PropertyValue.NotFound)){
                        nc.preparePost(wsession.pathBy("/users/" + botId).property(Const.Bot.RequestURL).stringValue())
                                .addParameter(Const.Message.Event, Const.Event.onFilter)
                                .addParameter(Const.Message.CausedEvent, pmap.get(PropertyId.fromIdString(Const.Message.Event)).stringValue())
                                .addParameter(Const.Message.Sender, pmap.get(PropertyId.fromIdString(Const.Message.Sender)).stringValue())
                                .addParameter(Const.Bot.BotId, botId)
                                .addParameter(Const.Message.Message, pmap.get(PropertyId.fromIdString(Const.Message.Message)).stringValue())
                                .addParameter(Const.Message.MessageId, messageId)
                                .addParameter(Const.Room.RoomId, roomId)
                                .execute().get();
                    }

                }



                Iterator<String> iter = wsession.pathBy("/rooms/" + roomId + "/members").childrenNames().iterator();

                while(iter.hasNext()){
                    String userId = iter.next();
                    if(receivers!=null && !receivers.asSet().contains(userId))
                        continue;

                    String randomID = new ObjectId().toString();
                    wsession.pathBy("/notifies/" + userId).property(Const.Notify.LastNotifyId, randomID)
                            .addChild(randomID)
                                .property(Const.Connection.DelegateServer, getDelegateServer(userId, wsession))
                                .property(Const.Notify.CreatedAt, ToonServer.GMTTime())
                                .refTo(Const.Message.Message, "/rooms/" + roomId + "/messages/" + messageId)
                                .refTo(Const.Room.RoomId, "/rooms/" + roomId);

                }
                if(PropertyValue.createPrimitive(Const.Event.onExit).equals(pmap.get(PropertyId.fromIdString(Const.Message.Event)))){
                    String sender = pmap.get(PropertyId.fromIdString(Const.Message.Sender)).stringValue();
                    String randomID = new ObjectId().toString();
                    wsession.pathBy("/notifies/" + sender).property(Const.Notify.LastNotifyId, randomID)
                            .addChild(randomID)
                            .property(Const.Connection.DelegateServer, getDelegateServer(sender, wsession))
                            .property(Const.Notify.CreatedAt, ToonServer.GMTTime())
                            .refTo(Const.Message.Message, "/rooms/" + roomId + "/messages/" + messageId)
                            .refTo(Const.Room.RoomId, "/rooms/" + roomId);
                }



                return null;
            }
        };
    }

    protected String getDelegateServer(String userId, ISession session) {

        if(session.exists("/users/" + userId))
            return session.pathBy("/users/" + userId).property(Const.Connection.DelegateServer).stringValue();
        else
            return session.workspace().repository().memberId();
    }

}
