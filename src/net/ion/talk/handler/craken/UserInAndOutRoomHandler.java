package net.ion.talk.handler.craken;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.listener.CDDModifiedEvent;
import net.ion.craken.listener.CDDRemovedEvent;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.bean.Const.Room;
import net.ion.talk.bean.Const.User;
import net.ion.talk.script.BotScript;

public class UserInAndOutRoomHandler implements CDDHandler {

	
	private BotScript bs;
	private UserInAndOutRoomHandler(BotScript bs) {
		this.bs = bs ;
	}

	public final static UserInAndOutRoomHandler create(BotScript bs){
		return new UserInAndOutRoomHandler(bs) ;
	}

	public static CDDHandler test() {
		return new UserInAndOutRoomHandler(null) ;
	}

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
            	ReadNode userNode = wsession.readSession().ghostBy("/bots/" + userId) ;
            	if (! userNode.isGhost()) {
            		wsession.pathBy("/rooms/" + roomId).refTos("bots", "/bots/"+  userId) ;
					bs.callFrom(userId, "whenIN", roomId) ;
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

            	ReadNode userNode = wsession.readSession().ghostBy("/bots/" + userId) ;
            	if (! userNode.isGhost()) {
            		wsession.pathBy("/rooms/" + roomId).unRefTos("bots", "/bots/"+  userId) ;
					bs.callFrom(userId, "whenOUT", roomId) ;
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
