package net.ion.talk.handler.craken;

import net.ion.craken.listener.CDDModifiedEvent;
import net.ion.craken.listener.CDDRemovedEvent;
import net.ion.craken.node.ISession;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.aclient.AsyncHttpProvider;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.client.IJsonRequest;
import net.ion.radon.client.ISerialRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.util.AradonTester;
import net.ion.talk.MockClient;
import net.ion.talk.TestCrakenBase;
import net.ion.talk.bean.Const;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 4. Time: 오후 2:43 To change this template use File | Settings | File Templates.
 */
public class TestTalkMessageHandler extends TestCrakenBase {
	private String memberId;
    private TalkMessageHandler handler;

    private String roomId = "testRoom";
    private String messageId = "testMessage";

    @Override
	public void setUp() throws Exception {
		super.setUp();

        NewClient nc = NewClient.create(ClientConfig.newBuilder().setRequestTimeoutInMs(3000).setMaxRequestRetry(3).setMaximumConnectionsPerHost(5).build());

        handler = new TalkMessageHandler(nc);
		memberId = rsession.workspace().repository().memberId();
        rsession.workspace().cddm().add(handler);

	}



    public void testPatternCheck() throws Exception{
        rsession.workspace().cddm().remove(handler);
        FakePatternHandler fakeHandler = new FakePatternHandler();
        rsession.workspace().cddm().add(fakeHandler);

        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/testRoom/messages/testMessage");
                return null;
            }
        });
        assertTrue(fakeHandler.patternIsRight);

    }

    public void testSendMessage() throws Exception {

        String msg = "Hello World!";

        addUserToRoom(roomId, "ryun", "alex");
        sendMessageToRoom(roomId, messageId, "alex", msg);

        ReadNode ryunNotifyMsg = getFirstNotify("ryun").ref(Const.Message.Message);
        ReadNode alexNotifyMsg = getFirstNotify("alex").ref(Const.Message.Message);

        assertTrue(rsession.exists("/notifies/ryun"));
        assertTrue(rsession.exists("/notifies/alex"));

		assertEquals(msg, ryunNotifyMsg.property(Const.Message.Message).stringValue());
        assertEquals(msg, alexNotifyMsg.property(Const.Message.Message).stringValue());

		assertEquals(rsession.ghostBy("/users/alex"), ryunNotifyMsg.ref(Const.Message.Sender));
		assertEquals(rsession.ghostBy("/users/alex"), alexNotifyMsg.ref(Const.Message.Sender));
	}

    public void testSpecificReceiver() throws Exception {

        addUserToRoom(roomId, "ryun", "alex");
        sendWhisperMessage(roomId, messageId, "alex", "Hello Ryun", "ryun");

        assertFalse(rsession.exists("/notifies/alex"));
		assertEquals(1, rsession.pathBy("/notifies/ryun/").children().toList().size());

	}

    public void testSyncBot() throws Exception {

//
//        createBotToCraken("testBot", "http://localhost:9000/test", true);
//        addUserToRoom(roomId, "ryun", "testBot");
//
//
//        Radon radon = Aradon.create(ConfigurationBuilder.newBuilder().aradon().sections().restSection("test").path("test").addUrlPattern("/").matchMode(EnumClass.IMatchMode.STARTWITH).handler(SyncBotLet.class).build()).toRadon(9000);
//        radon.start().get();
//
//        sendMessageToRoom(roomId, messageId, "ryun", "Hello World!");
//
//        new InfinityThread().startNJoin();
//        radon.stop();


    }

    public void testUserOutNotification() throws Exception {

        addUserToRoom(roomId, "ryun", "alex");
//        removeUserToRoom(roomId, "ryun");

        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/rooms/testRoom/members/ryun").removeSelf();

//                wsession.pathBy("/rooms/1234/messages/testMessage")
//                        .property(Const.Message.Message, "Bye")
//                        .property(Const.Message.Sender, "ryun")
//                        .property(Const.Message.Event, Const.Event.onExit);

                return null;
            }
        });

//        assertEquals(1, rsession.pathBy("/notifies/ryun/").children().toList().size());
//        assertEquals(1, rsession.pathBy("/notifies/alex/").children().toList().size());
//        Thread.sleep(1000);

//        new InfinityThread().startNJoin();

    }



    //TestSyncBot
    //TestNonSyncBot



    private class FakePatternHandler extends TalkMessageHandler{

        public boolean patternIsRight = false;

        public FakePatternHandler() {
            super(null);
        }

        @Override
        public String pathPattern() {
            return super.pathPattern();
        }

        @Override
        public TransactionJob<Void> deleted(Map<String, String> resolveMap, CDDRemovedEvent event) {
            patternIsRight = true;
            return null;
        }

        @Override
        public TransactionJob<Void> modified(Map<String, String> resolveMap, CDDModifiedEvent event) {
            patternIsRight = true;
            return null;
        }

        @Override
        protected String getDelegateServer(String userId, ISession session) {
            return null;
        }
    }

    private class SyncBotLet implements IServiceLet {

        public boolean isIncome = false;

        @Get
        public void haha(){
            this.isIncome = true;
            Debug.line();
        }
    }
}
