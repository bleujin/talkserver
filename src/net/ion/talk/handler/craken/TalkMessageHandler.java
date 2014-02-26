package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.*;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.talk.*;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bean.Const;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:44
 * To change this template use File | Settings | File Templates.
 */
public class TalkMessageHandler implements CDDHandler {

    public TalkMessageHandler() {
    }

    @Override
    public String pathPattern() {
        return "/rooms/{roomId}/messages/{messageId}";
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CacheEntryRemovedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {
        return null;
    }

    @Override
    public TransactionJob<Void> modified(Map<String, String> resolveMap, final CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String roomId = resolveMap.get("roomId");
        final String messageId = resolveMap.get("messageId");
        final PropertyValue receivers = event.getValue().get(PropertyId.fromIdString(Const.Message.Receivers));

        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                Iterator<String> iter = wsession.pathBy("/rooms/" + roomId + "/members").childrenNames().iterator();
                while(iter.hasNext()){
                    String userId = iter.next();
                    if(receivers!=null && !receivers.asSet().contains(userId))
                        continue;


                    String randomID = new ObjectId().toString();
                    wsession.pathBy("/notifies/" + userId).property("lastNotifyId", randomID)
                            .addChild(randomID)
                                .property("delegateServer", getDelegateServer(userId, wsession))
                                .property("createdAt", ToonServer.GMTTime())
                                .refTo(Const.Message.Message, "/rooms/" + roomId + "/messages/" + messageId)
                                .refTo(Const.Room.RoomId, "/rooms/" + roomId);

                }
                return null;
            }
        };
    }

    protected String getDelegateServer(String userId, ISession session) {

        if(session.exists("/connections/" + userId))
            return session.pathBy("/users/" + userId).property("delegateServer").stringValue();
        else
            return session.workspace().repository().memberId();
    }

}
