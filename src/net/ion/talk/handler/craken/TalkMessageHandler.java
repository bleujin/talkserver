package net.ion.talk.handler.craken;

import java.util.Map;
import java.util.Set;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.listener.CDDModifiedEvent;
import net.ion.craken.listener.CDDRemovedEvent;
import net.ion.craken.node.ISession;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.Bot;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.util.CalUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:44
 * To change this template use File | Settings | File Templates.
 */
public class TalkMessageHandler implements CDDHandler {

    private final NewClient nc;

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

                //유저에게 전송
                String roomId = resolveMap.get(Const.Room.RoomId) ;
                String messageId = resolveMap.get(Const.Message.MessageId) ;
                PropertyId exPropertyId = PropertyId.normal(Message.ExclusiveSender);
				boolean exclusiveSender = pmap.containsKey(exPropertyId) ? pmap.get(exPropertyId).asBoolean() : false ;
                String senderRef = pmap.get(PropertyId.refer(Message.Sender)).asString() ;
                String senderId = StringUtil.substringAfterLast(senderRef, "/");
                String options = pmap.get(PropertyId.normal(Const.Message.Options)).asString() ;
                
                
                ReadSession rsession = wsession.readSession() ;
                PropertyValue rvalue = ObjectUtil.coalesce(pmap.get(PropertyId.fromIdString(Const.Message.Receivers)), PropertyValue.NotFound);
                Set<String> receivers = rvalue.asSet() ;

				if (StringUtil.isBlank(rvalue.asString())){
					receivers = wsession.pathBy("/rooms/" + roomId + "/members").childrenNames(); 
				} 
				
				if (exclusiveSender){
					receivers.remove(senderId) ;
				}

                for(String receiverId : receivers){
                   	writeNotification(wsession, senderId, receiverId, roomId, messageId);
                }
                
                return null;
            }
        };
    }

    private void writeNotification(WriteSession wsession, String senderId, String receiver, String roomId, String messageId) {

    	ReadNode roomNode = wsession.readSession().pathBy("/rooms/" + roomId);
    	String svgURL = null ;
		if (roomNode.hasRef(Bot.PostBot)){
        	svgURL = "/svg/message/" + roomId + "/" + messageId + "?botId="  + roomNode.ref(Bot.PostBot).fqn().name() ;
    	} else {
    		svgURL = "/svg/message/" + roomId + "/" + messageId + "?botId=" ;
    	}
    	
        wsession.pathBy("/notifies/" + receiver).property(Const.Notify.LastNotifyId, messageId)
                .child(messageId)
                .property(Const.Connection.DelegateServer, getDelegateServer(receiver, wsession))
                .property(Const.Notify.CreatedAt, CalUtil.gmtTime())
                .property(Const.Notify.SVGUrl, svgURL)
                .property(Const.Notify.SenderId, senderId)
                .refTo(Const.Message.Message, "/rooms/" + roomId + "/messages/" + messageId)
                .refTo(Const.Room.RoomId, "/rooms/" + roomId);
    }

    protected String getDelegateServer(String userId, ISession session) {

        if(session.exists("/connections/" + userId))
            return session.pathBy("/connections/" + userId).property(Const.Connection.DelegateServer).stringValue();
        else
            return session.workspace().repository().memberId();
    }

}
