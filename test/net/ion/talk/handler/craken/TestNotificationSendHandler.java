package net.ion.talk.handler.craken;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.ObjectId;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;
import net.ion.talk.handler.engine.UserConnectionHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 11.
 * Time: 오후 2:06
 * To change this template use File | Settings | File Templates.
 */
public class TestNotificationSendHandler extends TestCase{

    private static String GCM_PUSH_KEY = "APA91bFQsheILrtWpk6e-x-ZH6MN" +
            "2BirntyZhEbmROoF5t0B0sJChWc7YvLbw0S8no6FBaNIo7vYy" +
            "G7sKFrq3PhGC3w8mk36gQxKo53zaBLGm3lMDxjIJuZl9L10u2UaLx" +
            "CZfOpvz2U81CeipH5GVMBlQ5wO-2KmeDZgwh6nlBnFQbhDIMgIYlY";

    private FakeWebSocketConnection user = FakeWebSocketConnection.create("ryun");
    private ReadSession rsession;
    private TalkEngine engine;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        engine = TalkEngine.test().registerHandler(new UserConnectionHandler()).startForTest();
        rsession = engine.readSession();
        NotificationListener notiHandler = new NotificationListener(engine, NotifyStrategy.createSender(engine.readSession()));
        rsession.workspace().addListener(notiHandler);

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/" + user.getString("id")).property("deviceOS", "android").property("pushId", GCM_PUSH_KEY);
                return null;
            }
        });

    }

    public String writeNotification(final String userId) throws Exception {

        final String randomId = new ObjectId().toString();
        final String roomId = new ObjectId().toString();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {


                //write
                wsession.pathBy("/notifies/" + userId).property("lastNotifyId", randomId)
                        .addChild(randomId)
                        .property("delegateServer", "emanon")
                        .property("isRead", "false")
                        .property("createdAt", ToonServer.GMTTime())
                        .refTo("message", "/rooms/" + roomId + "/messages/" + new ObjectId().toString())
                        .refTo("roomId", "/rooms/" + roomId);

                return null;
            }
        });

        return randomId;
    }

    public void testPush() throws Exception {

        engine.onOpen(user);
        Debug.line("websocketNoti");
        String webSocketNotification = writeNotification(user.getString("id"));
        engine.onClose(user);
        Debug.line("startPushMessage");
        writeNotification(user.getString("id"));
        Debug.line("asdf");

        Thread.sleep(1000);
        JsonObject receive = JsonObject.fromString(user.recentMsg());
        assertEquals(webSocketNotification, receive.asString("notifyId"));

    }


    @Override
    public void tearDown() throws Exception {
//        engine.stopForTest();
        super.tearDown();
    }

}
