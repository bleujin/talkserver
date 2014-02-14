package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.*;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.util.ObjectId;
import net.ion.talk.*;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:44
 * To change this template use File | Settings | File Templates.
 */
public class TalkMessageHandler implements CDDHandler, TalkHandler {

    private TalkEngine tengine;

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/messages/{messageId}";
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CacheEntryRemovedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {
        return null;
    }

    @Override
    public TransactionJob<Void> modified(Map<String, String> resolveMap, CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String roomId = resolveMap.get("roomId");
        final String messageId = resolveMap.get("messageId");


        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                Iterator<String> iter = wsession.pathBy("/rooms/" + roomId + "/members").childrenNames().iterator();


                while(iter.hasNext()){
                    String userId = iter.next();


                    //ifBot
                    if(wsession.pathBy("/users/"+userId).property("isBot").stringValue().equals("true")){
                        WriteNode messageNode = wsession.pathBy("/rooms/" + roomId + "/messages/" + messageId);
                        String message = messageNode.property("message").stringValue();
                        String sender = messageNode.property("sender").stringValue();
                        //"userId" variable is botId
                        tengine.botManager().onMessage(userId, message, sender);
                        continue;
                    }

                    String randomID = new ObjectId().toString();


                    //write
                    wsession.pathBy("/notifies/" + userId).property("lastNotifyId", randomID)
                            .addChild(randomID)
                                .property("delegateServer", getDelegateServer(userId, wsession))
                                .property("createdAt", ToonServer.GMTTime())
                                .refTo("message", "/rooms/" + roomId + "/messages/" + messageId)
                                .refTo("roomId", "/rooms/" + roomId);


                }
                return null;
            }
        };
    }

    protected String getDelegateServer(String userId, ISession session) {

        if(session.exists("/connections/" + userId))
            return session.pathBy("/users/" + userId).property("delegateServer").stringValue();
        else
            return session.workspace().repository().memberId();
    }


    @Override
    public void onConnected(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws IOException {
        this.tengine = tengine;
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }
}
