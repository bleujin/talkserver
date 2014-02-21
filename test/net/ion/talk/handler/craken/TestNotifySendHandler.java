package net.ion.talk.handler.craken;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 21.
 * Time: 오후 4:34
 * To change this template use File | Settings | File Templates.
 */
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.ion.craken.node.AbstractReadSession;
import net.ion.radon.core.Aradon;
import net.ion.talk.account.AccountManager;
import sun.nio.cs.MS1250;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.message.push.sender.PushMessage;
import net.ion.message.push.sender.PushResponse;
import net.ion.message.push.sender.Sender;
import net.ion.message.push.sender.SenderConfig;
import net.ion.message.push.sender.strategy.PushStrategy;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.util.AradonTester;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;
import net.ion.talk.UserConnection;
import net.ion.talk.responsebuilder.TalkResponse;
import junit.framework.TestCase;

public class TestNotifySendHandler extends TestCase {

    private ReadSession rsession;
    private FakeTalkEngine tengine;
    private FakeSender sender;
    private String memberId;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        RepositoryEntry rentry = RepositoryEntry.test();
        rsession = rentry.login();
        tengine = new FakeTalkEngine(AradonTester.create().getAradon(), rsession);
        sender = new FakeSender();
        NotificationListener handler = new NotificationListener(new AccountManager(tengine, sender));
        rsession.workspace().addListener(handler) ;
        memberId = rsession.workspace().repository().memberId() ;



    }

    private void writeNotify(final String memberId) throws Exception {
        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                String notifyID = "1234";
                String userId = "bleujin";
                String roomId = "roomId";
                String messageId = "messageId";

                wsession.pathBy("/users/bleujin");
                wsession.pathBy("/notifies/" + userId)
                        .property("lastNotifyId", notifyID).addChild(notifyID)
                        .property("delegateServer", memberId).property("createdAt", ToonServer.GMTTime()).refTo("message", "/rooms/" + roomId + "/messages/" + messageId).refTo("roomId", "/rooms/" + roomId);
                return null;
            }
        });
        Thread.sleep(1000);
    }

    public void testDisconnected() throws Exception {
        writeNotify(memberId);
        String pushReceived = sender.sendeds.get("bleujin").received() ;
        assertEquals("{\"notifyId\":\"1234\"}", pushReceived);
    }

    public void testConnected() throws Exception {
        tengine.users.put("bleujin", new FakeUserConnection(null));
        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/connections/bleujin").refTo("user", "/users/bleujin");
                return null;
            }
        });

        writeNotify(memberId);
        FakeUserConnection bleujin = tengine.findConnection("bleujin") ;
        String msg = bleujin.receivedMessage() ;
        assertEquals("{\"notifyId\":\"1234\"}", msg);

    }

    @Override
    public void tearDown() throws Exception {
        rsession.workspace().repository().shutdown();
        super.tearDown();
    }
}


class FakeTalkEngine extends TalkEngine{

    private final ReadSession rsession;
    private final Aradon aradon;
    public Map<String, FakeUserConnection> users = MapUtil.newMap() ;
    public FakeTalkEngine(Aradon aradon, ReadSession rsession) throws Exception{
        super(aradon) ;
        this.aradon = aradon;
        aradon.getServiceContext().putAttribute(Sender.class.getCanonicalName(), Sender.class);
        this.rsession = rsession;
    }

    public FakeUserConnection findConnection(String userId){
        return users.get(userId) ;
    }

    @Override
    public ReadSession readSession() throws IOException {
        return rsession;
    }

    @Override
    public void sendMessage(String userId, Sender sender, TalkResponse tresponse){
        super.sendMessage(userId, sender, tresponse);
    }
}

class FakeUserConnection extends UserConnection {
    private String received;
    protected FakeUserConnection(WebSocketConnection inner) {
        super(inner);
    }

    public String receivedMessage() {
        return received;
    }

    public void sendMessage(String message) {
        this.received = message ;
    }
}

class FakeSender extends Sender{

    public Map<String, FakePushMessage> sendeds = MapUtil.newMap() ;
    protected FakeSender() {
        super(null, null, null);
    }

    @Override
    public FakePushMessage sendTo(String... receiver) {
        FakePushMessage result = new FakePushMessage(this, receiver) ;
        sendeds.put(StringUtil.join(receiver), result) ;
        return result ;
    }

}

class FakePushMessage extends PushMessage{
    private String received;

    public FakePushMessage(Sender sender, String[] receivers) {
        super(sender, receivers);
    }

    @Override
    public Future<List<PushResponse>> sendAsync(String message) {
        this.received = message ;
        return null ;
    }

    public String received(){
        return received ;
    }

}
