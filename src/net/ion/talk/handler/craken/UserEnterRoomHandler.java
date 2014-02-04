package net.ion.talk.handler.craken;

import java.util.Iterator;
import java.util.Map;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;

import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 6:27
 * To change this template use File | Settings | File Templates.
 */
public class UserEnterRoomHandler implements CDDHandler {
    private final ReadSession rsession;

    public UserEnterRoomHandler(ReadSession rsession) {
        this.rsession = rsession;
    }

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/members/{userId}";
    }

    @Override
    public TransactionJob<Void> modified(final Map<String, String> resolveMap, CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String roomId = resolveMap.get("roomId");
        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                Iterator<String> iter = wsession.pathBy("/rooms/" + roomId + "/members").childrenNames().iterator();
                while(iter.hasNext()){
                    String userId = iter.next();
                    int newNotifyId = wsession.pathBy("/notifies/" + userId).property("lastNotifyId").intValue(0)+1;
                    wsession.pathBy("/notifies/" + userId).property("lastNotifyId", newNotifyId);
                    wsession.pathBy("/notifies/" + userId).addChild(String.valueOf(newNotifyId))
                            .property("delegateServer", wsession.workspace().repository().memberId())
                    .property("createdAt", System.currentTimeMillis())
                            //will define message
                    .property("messageId", userId + "entered at room:" + roomId)
                    .property("roomId", roomId);
                }
                return null;
            }
        };
    }

	@Override
	public TransactionJob<Void> deleted(Map<String, String> resolveMap, CacheEntryRemovedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {
		return null;
	}
}
