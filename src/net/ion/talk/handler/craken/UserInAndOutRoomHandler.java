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

        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                String randomID = new ObjectId().toString();

//                String sender = wsession.pathBy("/rooms/" + roomId + "/members/"+userId).property(Const.Message.Sender).stringValue();

                wsession.pathBy("/rooms/" + roomId + "/messages/")
                        .child(randomID)
                        .property(Const.Message.Event, Const.Event.onEnter)
                        .property(Const.Room.RoomId, roomId)
                        .property(Const.Message.Time, Calendar.getInstance().getTimeInMillis())
                        .property(Const.Message.Message, userId)
                        .refTo(Const.Message.Sender, userId)
                        .property(Const.Message.MessageId, randomID);

                return null;
            }
        };
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CDDRemovedEvent event) {

        final String roomId = resolveMap.get(Const.Room.RoomId);
        final String userId = resolveMap.get(Const.User.UserId);
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

//                String sender = wsession.pathBy("/rooms/" + roomId + "/members/"+userId).property(Const.Message.Sender).stringValue();

                String randomID = new ObjectId().toString();


//                wsession.pathBy("/rooms/1234/messages/testMessage")
//                        .property(Const.Message.Message, "Bye")
//                        .property(Const.Message.Sender, "ryun")
//                        .property(Const.Message.Event, Const.Event.onExit);
                //will define message
                wsession.pathBy("/rooms/" + roomId + "/messages/")
                        .child(randomID)
                        .property(Const.Message.Event, Const.Event.onExit)
                        .property(Const.Room.RoomId, roomId)
                        .property(Const.Message.Time, Calendar.getInstance().getTimeInMillis())
                        .property(Const.Message.Message, userId)
                        .refTo(Const.Message.Sender, userId)
                        .property(Const.Message.MessageId, randomID);
                return null;
            }
        };
    }
}
