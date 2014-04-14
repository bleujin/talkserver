package net.ion.talk.handler.craken;

import net.ion.craken.listener.AsyncCDDHandler;
import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.TreeNodeKey;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 6:27
 * To change this template use File | Settings | File Templates.
 */
public class UserInAndOutRoomHandler implements AsyncCDDHandler {

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/members/{userId}";
    }

    @Override
    public TransactionJob<Void> modified(Map<String, String> resolveMap, CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

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
                        .property(Const.Message.Message, userId + "님이 입장하셨습니다.")
                        .property(Const.Message.Sender, userId)
                        .property(Const.Message.MessageId, randomID);

                return null;
            }
        };
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CacheEntryRemovedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String roomId = resolveMap.get(Const.Room.RoomId);
        final String userId = resolveMap.get(Const.User.UserId);
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

//                String sender = wsession.pathBy("/rooms/" + roomId + "/members/"+userId).property(Const.Message.Sender).stringValue();

                String randomID = new ObjectId().toString();

                //will define message
                wsession.pathBy("/rooms/" + roomId + "/messages/")
                        .child(randomID)
                        .property(Const.Message.Event, Const.Event.onExit)
                        .property(Const.Room.RoomId, roomId)
                        .property(Const.Message.Message, userId + "님이 퇴장하셨습니다.")
                        .property(Const.Message.Sender, userId)
                        .property(Const.Message.MessageId, randomID);
                return null;
            }
        };
    }
}
