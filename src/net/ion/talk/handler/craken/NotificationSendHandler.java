package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.message.push.sender.Sender;
import net.ion.message.push.sender.SenderConfig;
import net.ion.message.push.sender.Vender;
import net.ion.message.push.sender.strategy.PushStrategy;
import net.ion.talk.*;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 11.
 * Time: 오후 2:08
 * To change this template use File | Settings | File Templates.
 */
public class NotificationSendHandler implements CDDHandler, TalkHandler {

    public static String GCM_API_KEY = "AIzaSyBC_YDd2WfKy_K3T7r5PQo3M_dMfg5k5WA";
    public static String KEY_STORE_PATH = "./resource/keystore/toontalk.p12";
    public static String PASSWORD = "toontalk";


    private ExecutorService es;
    private TalkEngine tengine;
    private SenderConfig config = SenderConfig.newBuilder()
                    .googleConfig(GCM_API_KEY)
                    .appleConfig(KEY_STORE_PATH, PASSWORD, false)
                    .retryAttempts(3).retryAfter(5, TimeUnit.MINUTES).build();;
    private NotificationStrategy notiStrategy;

    @Override
    public String pathPattern() {
        return "/notifies/{userId}/{notifyId}";
    }

    @Override
    public TransactionJob<Void> modified(Map<String, String> resolveMap, CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

        final String userId = resolveMap.get("userId");
        final String notifyId = resolveMap.get("notifyId");

        return new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                boolean isConnected = tengine.isUserExist(userId);
                boolean delegateIsMime = wsession.pathBy("/users/"+userId).property("delegateServer").stringValue()
                        .equals(wsession.workspace().repository().memberId());

                if(isConnected && delegateIsMime){
                    UserConnection uconn = tengine.getUserConnection(userId);
                    JsonObject response = TalkResponseBuilder.create().newInner().property("notifyId", notifyId).build().toJsonObject();
                    uconn.sendMessage(response.toString());

                }else if(!isConnected && delegateIsMime){
                    String pushMessage = TalkResponseBuilder.create().newInner().property("notifyId", notifyId).build().toString();
                    config.createSender(notiStrategy).sendTo(userId).sendAsync(pushMessage);
                }

                return null;
            }
        };
    }

    @Override
    public TransactionJob<Void> deleted(Map<String, String> resolveMap, CacheEntryRemovedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {
        return null;
    }

    @Override
    public void onConnected(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws IOException {
        this.tengine = tengine;
        this.notiStrategy = new NotificationStrategy(tengine.readSession());
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }

    class NotificationStrategy implements PushStrategy {

        private final ReadSession rsession;

        NotificationStrategy(ReadSession rsession) {
            this.rsession = rsession;
        }

        @Override
        public int getBadge() {
            return 1;
        }

        @Override
        public String getSound() {
            return null;
        }

        @Override
        public int getTimeToLive() {
            return 0;
        }

        @Override
        public String getCollapseKey() {
            return null;
        }

        @Override
        public boolean getDelayWhenIdle() {
            return false;
        }

        @Override
        public Vender vender(String targetId) {
            if(rsession.pathBy("/users/" + targetId).property("deviceOS").stringValue().equals("apple"))
                return Vender.APPLE;
            else
                return Vender.GOOGLE;
        }

        @Override
        public String deviceId(String targetId) {
            return rsession.pathBy("/users/"+targetId).property("pushId").stringValue();
        }
    }
}
