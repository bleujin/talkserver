package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.talk.ToonServer;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:44
 * To change this template use File | Settings | File Templates.
 */
public class TalkMessageHandler implements CDDHandler {
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

                    //get UserId
                    String userId = iter.next();

                    //prepare data
                    String randomID = new ObjectId().toString();
                    Object isConnected = wsession.pathBy("/users/" + userId).property("isConnected").value();

                    String delegateServer;
                    if(isConnected.equals(false))
                        delegateServer = wsession.workspace().repository().memberId();
                    else
                        delegateServer = wsession.pathBy("/users/" + userId).property("server").stringValue();

                    //write
                    WriteNode userNoti = wsession.pathBy("/notifies/" + userId + "/" + roomId);
                    userNoti.property("lastNotifyId", randomID)
                            .addChild(randomID)
                            .property("delegateServer", delegateServer)
                            .property("createdAt", ToonServer.GMTTime())
                            .refTo("message", "/rooms/" + roomId + "/messages/" + messageId)
                            .property("roomId", roomId);

                }
                return null;

            }
        };
    }
}
