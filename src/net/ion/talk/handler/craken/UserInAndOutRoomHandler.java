package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.util.ObjectId;
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
public class UserInAndOutRoomHandler implements CDDHandler {

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/members/{userId}";
    }

    @Override
    public TransactionJob<Void> modified(Map<String, String> resolveMap, CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String roomId = resolveMap.get("roomId");
        final String userId = resolveMap.get("userId");
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                    String randomID = new ObjectId().toString();

                    wsession.pathBy("/rooms/" + roomId + "/messages/")
                            .addChild(randomID)
                                .property("message", "ROOM|"+ roomId + "|ENTER|" + userId)
                                .property("status", "smile")
                                .refTo("sender", "/users/" + userId);

                return null;
            }
        };
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CacheEntryRemovedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String roomId = resolveMap.get("roomId");
        final String userId = resolveMap.get("userId");
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                    String randomID = new ObjectId().toString();

                    //will define message
                    wsession.pathBy("/rooms/"+roomId+"/messages/")
                            .addChild(randomID)
                            .property("message", "ROOM|"+ roomId + "|EXIT|" + userId)
                            .property("status", "smile")
                            .refTo("sender", "/users/"+userId);
                return null;
            }
        };
    }
}
