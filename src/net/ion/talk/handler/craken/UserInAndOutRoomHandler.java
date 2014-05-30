package net.ion.talk.handler.craken;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.listener.CDDModifiedEvent;
import net.ion.craken.listener.CDDRemovedEvent;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.bean.Const.Room;
import net.ion.talk.bean.Const.User;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 6:27
 * To change this template use File | Settings | File Templates.
 */
public class UserInAndOutRoomHandler implements CDDHandler {

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/members/{userId}";
    }

    @Override
    public TransactionJob<Void> modified(Map<String, String> resolveMap, CDDModifiedEvent event) {

        final String roomId = resolveMap.get(Const.Room.RoomId);
        final String userId = resolveMap.get(Const.User.UserId);

        if (roomId.startsWith("@")) return null ;
        
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
            	
            	if (wsession.readSession().ghostBy("/bots/" + userId).property("owner").asBoolean()){
                	wsession.pathBy("/rooms/" + roomId).refTos("owner", "/bots/"+userId) ;
            	}
            	
            	
                String messageId = new ObjectId().toString();
                wsession.pathBy("/rooms/" + roomId + "/messages/")
                        .child(messageId)
                    //    .property(Message.ExclusiveSender, true) 
                        .property(Message.Options, "{event:'onEnter'}")
                        .property(Room.RoomId, roomId)
                        .property(Message.Time, Calendar.getInstance().getTimeInMillis())
                        .property(Message.Message, userId + " enter")                      
                        .property(Message.ClientScript, Message.DefaultOnMessageClientScript) 
                        .refTo(Message.Sender, "/users/"+userId)
                        .property(Message.MessageId, messageId);
                return null;
            }
        };
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CDDRemovedEvent event) {

        final String roomId = resolveMap.get(Room.RoomId);
        final String userId = resolveMap.get(User.UserId);

        if (roomId.startsWith("@")) return null ;
        
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

            	if (wsession.readSession().ghostBy("/bots/" + userId).property("owner").asBoolean()){
                	wsession.pathBy("/rooms/" + roomId).unRefTos("owner", "/bots/"+userId) ;
            	}

            	
                String messageId = new ObjectId().toString();
                //will define message
                wsession.pathBy("/rooms/" + roomId + "/messages/")
                        .child(messageId)
                        .property(Message.ExclusiveSender, true) 
                        .property(Message.Options, "{event:'onExit'}")
                        		
                        .property(Room.RoomId, roomId)
                        .property(Message.Time, Calendar.getInstance().getTimeInMillis())
                        .property(Message.Message, userId + " exit")
                        .property(Message.ClientScript, Message.DefaultOnMessageClientScript) 
                        .refTo(Message.Sender, "/users/"+userId)
                        .property(Message.MessageId, messageId);

                return null;
            }
        };
    }
}
