package net.ion.talk.handler.craken;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.listener.CDDModifiedEvent;
import net.ion.craken.listener.CDDRemovedEvent;
import net.ion.craken.node.ISession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.WriteChildren;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.ToonServer;
import net.ion.talk.bean.Const;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:44
 * To change this template use File | Settings | File Templates.
 */
public class TalkMessageHandler implements CDDHandler {

    private final NewClient nc;

    private static final Predicate<WriteNode> SYNCBotFilter = new Predicate<WriteNode>() {
        @Override
        public boolean apply(WriteNode userNode) {
            if(!userNode.hasRef(Const.Ref.User)) return false;

            return userNode.ref(Const.Ref.User).property(Const.Bot.isSyncBot).asBoolean();
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


        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                Map<PropertyId, PropertyValue> pmap = event.getValue() ;

                //Syncbot에 의한 Filter 메지시 이벤트면 무시
//                if (isFilterMessage(pmap)) return null;

                //SyncBot이 있으면 메시지 선 처리.
                
//                WriteChildren syncBots = wsession.pathBy("/rooms/" + getRoomId(resolveMap) + "/members").children().filter(SYNCBotFilter);
//                sendMessageToSyncBot(wsession, resolveMap, pmap, syncBots);

                //유저에게 전송
                String roomId = resolveMap.get(Const.Room.RoomId) ;
                String messageId = resolveMap.get(Const.Message.MessageId) ;
                
//                Debug.line('x', roomId, wsession.pathBy("/rooms/roomroom/messages/" + messageId).transformer(net.ion.craken.node.convert.Functions.WRITE_TOFLATMAP));
                Set<String> users = wsession.pathBy("/rooms/" + roomId + "/members").childrenNames();
                for(String userId : users){
                    //귓속말에 자기 자신이 없으면 continue;
                    Set receivers = getReceivers(pmap).asSet() ;
                    if (receivers.size() == 0 || receivers.contains(userId))  
                    	writeNotification(wsession, userId, roomId, messageId);
                }
                

                //유저의 방 나감 이벤트일 경우 처리
//                ifUserOnExit(wsession, resolveMap, pmap);


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

    private void writeNotification(WriteSession wsession, String receiver, String roomId, String messageId) {
//        String randomID = new ObjectId().toString();
        wsession.pathBy("/notifies/" + receiver).property(Const.Notify.LastNotifyId, messageId)
                .child(messageId)
                .property(Const.Connection.DelegateServer, getDelegateServer(receiver, wsession))
                .property(Const.Notify.CreatedAt, ToonServer.GMTTime())
                .refTo(Const.Message.Message, "/rooms/" + roomId + "/messages/" + messageId)
                .refTo(Const.Room.RoomId, "/rooms/" + roomId);
    }

    protected String getDelegateServer(String userId, ISession session) {

        if(session.exists("/connections/" + userId))
            return session.pathBy("/connections/" + userId).property(Const.Connection.DelegateServer).stringValue();
        else
            return session.workspace().repository().memberId();
    }

    private boolean isFilterMessage(Map<PropertyId, PropertyValue> pmap) {
        return (pmap.get(PropertyId.fromIdString(Const.Message.Filter)) != null);
    }

    private boolean ignoreNonWhisper(String botId, PropertyValue receivers) {
        return (StringUtil.isNotBlank(receivers.asString()) && !receivers.asSet().contains(botId));
    }

    private PropertyValue getReceivers(Map<PropertyId, PropertyValue> pmap) {
        return ObjectUtil.coalesce(pmap.get(PropertyId.fromIdString(Const.Message.Receivers)), PropertyValue.NotFound);
    }

    private String getMessageId(Map<String, String> resolveMap) {
        return resolveMap.get(Const.Message.MessageId);
    }

    private String getRoomId(Map<String, String> resolveMap) {
        return resolveMap.get(Const.Room.RoomId);
    }
}
