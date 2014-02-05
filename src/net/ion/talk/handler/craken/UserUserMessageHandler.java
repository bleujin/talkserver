package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.util.Debug;
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
public class UserUserMessageHandler implements CDDHandler {
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
                    WriteNode userNoti = wsession.pathBy("/notifies/" + userId + "/" + roomId);

                    int newNotifyId = userNoti.property("lastNotifyId").intValue(0)+1;
                    userNoti.property("lastNotifyId", newNotifyId)
                            .addChild(String.valueOf(newNotifyId))
                            .property("delegateServer", wsession.workspace().repository().memberId())
                            .property("createdAt", System.currentTimeMillis())
                            .refTo("message", "/rooms/" + roomId + "/messages/" + messageId)
                            .property("roomId", roomId);
                }
                return null;

            }
        };
    }
}
