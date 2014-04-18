package net.ion.talk.handler.craken;

import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import net.ion.craken.listener.CDDHandler;
import net.ion.craken.listener.CDDModifiedEvent;
import net.ion.craken.listener.CDDRemovedEvent;
import net.ion.craken.node.*;
import net.ion.craken.node.crud.WriteChildren;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.*;
import net.ion.talk.bean.Const;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:44
 * To change this template use File | Settings | File Templates.
 */
public class TalkMessageHandler implements CDDHandler {

    private final NewClient nc;

    private static final Predicate<WriteNode> syncBotFilter = new Predicate<WriteNode>() {
        @Override
        public boolean apply(@Nullable WriteNode userNode) {
            if(!userNode.hasRef(Const.Ref.User)) return false;

            return (userNode.ref(Const.Ref.User).property(Const.Bot.isSyncBot).stringValue().equals("true"));
        }
    };

    public TalkMessageHandler(NewClient nc) {
        this.nc = nc;
    }

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/messages/{messageId}";
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CDDRemovedEvent event) {
        return null;
    }

    @Override
    public TransactionJob<Void> modified(final Map<String, String> resolveMap, final CDDModifiedEvent event) {


        Debug.line(event);
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                Map<PropertyId, PropertyValue> pmap = event.getValue() ;

                //Syncbot에 의한 Filter 메지시 이벤트면 무시
                if (isFilterMessage(pmap)) return null;

                //SyncBot이 있으면 메시지 선 처리.
                WriteChildren syncBots = wsession.pathBy("/rooms/" + getRoomId(resolveMap) + "/members").children().filter(syncBotFilter);
                sendMessageToSyncBot(wsession, resolveMap, pmap, syncBots);

                //유저에게 전송
                sendMessageToUsers(wsession, resolveMap, pmap);

                //유저의 방 나감 이벤트일 경우 처리
                ifUserOnExit(wsession, resolveMap, pmap);


                return null;
            }
        };
    }

    private void ifUserOnExit(WriteSession wsession, Map<String,String> resolveMap, Map<PropertyId,PropertyValue> pmap) {
        //유저 퇴장시 방의 유저에게 퇴장 Notify 생성
        if(PropertyValue.createPrimitive(Const.Event.onExit).equals(pmap.get(PropertyId.fromIdString(Const.Message.Event)))){
            String sender = pmap.get(PropertyId.fromIdString(Const.Message.Sender)).stringValue();
            writeNotification(wsession, sender, getRoomId(resolveMap), getMessageId(resolveMap));
        }
    }

    private void sendMessageToSyncBot(WriteSession wsession, Map<String, String> resolveMap, Map<PropertyId, PropertyValue> pmap, WriteChildren syncBots) throws IOException, ExecutionException, InterruptedException {
        for(WriteNode botRef : syncBots){
            String botId = botRef.fqn().name();

            //귓속말에 자기 자신이 없으면 continue;
            if (ignoreNonWhisper(botId, getReceivers(pmap))) continue;

            if(wsession.exists("/bots/"+botId) && wsession.pathBy("/bots/"+botId).ref("bot").property(Const.Bot.isSyncBot).stringValue().equals("true")){
                nc.preparePost(wsession.pathBy("/users/" + botId).property(Const.Bot.RequestURL).stringValue())
                        .addParameter(Const.Message.Event, Const.Event.onFilter)
                        .addParameter(Const.Message.CausedEvent, pmap.get(PropertyId.fromIdString(Const.Message.Event)).stringValue())
                        .addParameter(Const.Message.Sender, pmap.get(PropertyId.fromIdString(Const.Message.Sender)).stringValue())
                        .addParameter(Const.Bot.BotId, botId)
                        .addParameter(Const.Message.Message, pmap.get(PropertyId.fromIdString(Const.Message.Message)).stringValue())
                        .addParameter(Const.Message.MessageId, getMessageId(resolveMap))
                        .addParameter(Const.Room.RoomId, getRoomId(resolveMap))
                        .execute().get();
            }
        }
    }

    private void sendMessageToUsers(WriteSession wsession, Map<String, String> resolveMap, Map<PropertyId, PropertyValue> pmap) {
        Set<String> users = wsession.pathBy("/rooms/" + getRoomId(resolveMap) + "/members").childrenNames();

        for(String userId : users){

            //귓속말에 자기 자신이 없으면 continue;
            if (ignoreNonWhisper(userId, getReceivers(pmap))) continue;
            writeNotification(wsession, userId, getRoomId(resolveMap), getMessageId(resolveMap));
        }
    }

    private void writeNotification(WriteSession wsession, String sender, String roomId, String messageId) {
        String randomID = new ObjectId().toString();
        wsession.pathBy("/notifies/" + sender).property(Const.Notify.LastNotifyId, randomID)
                .child(randomID)
                .property(Const.Connection.DelegateServer, getDelegateServer(sender, wsession))
                .property(Const.Notify.CreatedAt, ToonServer.GMTTime())
                .refTo(Const.Message.Message, "/rooms/" + roomId + "/messages/" + messageId)
                .refTo(Const.Room.RoomId, "/rooms/" + roomId);
    }

    protected String getDelegateServer(String userId, ISession session) {

        if(session.exists("/users/" + userId) && !session.exists("/bots/" + userId))
            return session.pathBy("/users/" + userId).property(Const.Connection.DelegateServer).stringValue();
        else
            return session.workspace().repository().memberId();
    }

    private boolean isFilterMessage(Map<PropertyId, PropertyValue> pmap) {
        return (pmap.get(PropertyId.fromIdString(Const.Message.Filter)) != null);
    }

    private boolean ignoreNonWhisper(String botId, PropertyValue receivers) {
        return (receivers!=null && !receivers.asSet().contains(botId));
    }

    private PropertyValue getReceivers(Map<PropertyId, PropertyValue> pmap) {
        return pmap.get(PropertyId.fromIdString(Const.Message.Receivers));
    }

    private String getMessageId(Map<String, String> resolveMap) {
        return resolveMap.get(Const.Message.MessageId);
    }

    private String getRoomId(Map<String, String> resolveMap) {
        return resolveMap.get(Const.Room.RoomId);
    }
}
