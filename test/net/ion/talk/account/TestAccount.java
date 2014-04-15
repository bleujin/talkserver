package net.ion.talk.account;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.message.push.sender.PushMessage;
import net.ion.message.push.sender.PushResponse;
import net.ion.message.push.sender.Sender;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.aclient.ClientConfigBean;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.Aradon;
import net.ion.radon.util.AradonTester;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 20. Time: 오후 3:56 To change this template use File | Settings | File Templates.
 */
public class TestAccount extends TestCase {

	private ReadSession session;
	private Account connectedUser;
	private Account bot;
	private Account disconnectedUser;
	private Account notFoundUser;
    private Aradon aradon;

    @Override
	public void setUp() throws Exception {
		RepositoryEntry rentry = RepositoryEntry.test();
		rentry.repository().start() ;
		session = rentry.login();

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/bleujin").property("name", "bleujin");
				wsession.pathBy("/users/ryun").property("name", "ryun");
				wsession.pathBy("/users/bot").property("name", "bot").property("requestURL", "http://www.daum.net");
				wsession.pathBy("/connections/bleujin").refTo("user", "/users/bleujin");
				return null;
			}
		});

        aradon = AradonTester.create().getAradon();

        FakeTalkEngine fakeTalkEngine = new FakeTalkEngine(aradon, rentry);
		fakeTalkEngine.users.put("bleujin", new FakeUserConnection(null));

		AccountManager am = AccountManager.create(fakeTalkEngine, new FakeSender());

		connectedUser = am.newAccount("bleujin");
		bot = am.newAccount("bot");
		disconnectedUser = am.newAccount("ryun");
		notFoundUser = am.newAccount("notFound");
	}

    @Override
    public void tearDown() throws Exception {
        session.workspace().repository().shutdown();
	}



    public void testBot() throws Exception {

        session.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/testRoom/messages/testMessage")
                        .property("sender", "ryun")
                        .property("message","Hello World!")
                        .property(Const.Message.Event, Const.Event.onMessage);

                wsession.pathBy("/notifies/bot/test").refTo ("message", "/rooms/testRoom/messages/testMessage").refTo("roomId", "/rooms/testRoom");
                return null;
            }
        });


        Thread.sleep(2000);
        Debug.line(session.pathBy("/users/"));

        System.gc();System.gc();System.gc();
        TalkResponse response = TalkResponseBuilder.create().newInner().property("notifyId", "test").build();
        assertEquals(200, bot.onMessage(response));
    }


    public void testWhenNotConnectedUser() throws Exception {

        TalkResponse response = TalkResponseBuilder.create().newInner().build();

        disconnectedUser.onMessage(response);
        String received = ((FakeSender) ((DisconnectedAccount) disconnectedUser).sender()).sendeds.get("ryun").received();
        assertEquals("{}", received);
    }

    public void testWhenConnectedUser() throws Exception {
        TalkResponse response = TalkResponseBuilder.create().newInner().build();
        connectedUser.onMessage(response);
        String received = ((FakeUserConnection) ((ConnectedUserAccount) connectedUser).userConnection()).receivedMessage();
        assertEquals("{}", received);
    }

    public void testNotFoundUser() throws InterruptedException, ExecutionException, IOException {
        TalkResponse response = TalkResponseBuilder.create().newInner().build();
        assertNull(notFoundUser.onMessage(response));
    }


}

class FakeTalkEngine extends TalkEngine {

	public Map<String, FakeUserConnection> users = MapUtil.newMap();

	FakeTalkEngine(Aradon aradon, RepositoryEntry rentry) throws IOException {
		super(aradon);
		context().putAttribute(RepositoryEntry.EntryName, rentry);
		context().putAttribute(Sender.class.getCanonicalName(), new FakeSender());
		context().putAttribute(NewClient.class.getCanonicalName(), NewClient.create(new ClientConfigBean().setMaxConnectionPerHost(5).setMaxRequestRetry(2)));
	}

	public FakeUserConnection findConnection(String userId) {
		return users.get(userId);
	}

	@Override
	public void sendMessage(String userId, Sender sender, TalkResponse tresponse) {
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
		this.received = message;
	}
}

class FakeSender extends Sender {

	public Map<String, FakePushMessage> sendeds = MapUtil.newMap();

	protected FakeSender() {
		super(null, null, null);
	}

	@Override
	public FakePushMessage sendTo(String... receiver) {
		FakePushMessage result = new FakePushMessage(this, receiver);
		sendeds.put(StringUtil.join(receiver), result);
		return result;
	}

}

class FakePushMessage extends PushMessage {
	private String received;

	public FakePushMessage(Sender sender, String[] receivers) {
		super(sender, receivers);
	}

	@Override
	public Future<List<PushResponse>> sendAsync(String message) {
		this.received = message;
		return null;
	}

	public String received() {
		return received;
	}

}
