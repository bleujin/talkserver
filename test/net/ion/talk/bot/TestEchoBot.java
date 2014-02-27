package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.Aradon;
import net.ion.radon.util.AradonTester;
import net.ion.talk.account.Bot;
import net.ion.talk.bean.Const;
import net.ion.talk.let.EmbedBotLet;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 25.
 * Time: 오전 11:34
 * To change this template use File | Settings | File Templates.
 */
public class TestEchoBot extends TestCase{

    private RepositoryEntry rentry;
    private ReadSession rsession;
    private RhinoEntry rengine;
    private EchoBot echoBot;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rentry = RepositoryEntry.test();
        rengine = RhinoEntry.test();
        rengine.startForTest();
        rsession = rentry.login();
        echoBot = new EchoBot(rsession);


        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/members/ryun");
                wsession.pathBy("/rooms/test/members/alex");
                wsession.pathBy("/rooms/test/members/echoBot");
                return null;
            }
        });
    }


    public void testOnEnter() throws Exception {
        echoBot.onEnter("test", "ryun");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().next();
        assertEquals("Hello! ryun", messageNode.property(Const.Message.Message).stringValue());
    }

    public void testOnExit() throws Exception {
        echoBot.onExit("test", "ryun");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().next();
        assertEquals("Bye! ryun", messageNode.property(Const.Message.Message).stringValue());

    }

    public void testOnMessage() throws Exception {
        echoBot.onMessage("test", "ryun", "Everybody Hello!");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().next();
        assertEquals("Everybody Hello!", messageNode.property(Const.Message.Message).stringValue());
    }


    public void testEchoBot() throws Exception {

        BotManager botManager = BotManager.create(rsession);
        botManager.registerBot(new EchoBot(rsession));

        Aradon aradon = AradonTester.create().register("", "/bot",  EmbedBotLet.class).getAradon().startServer(9000);
        aradon.getServiceContext().putAttribute(RepositoryEntry.EntryName, rentry);
        aradon.getServiceContext().putAttribute(RhinoEntry.EntryName, rengine);
        aradon.getServiceContext().putAttribute(BotManager.class.getCanonicalName(), botManager);


        final String notifyId = new ObjectId().toString();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/1234/members/echoBot");
                wsession.pathBy("/rooms/1234/members/ryun");

                wsession.pathBy("/rooms/1234/messages/testMessage")
                        .property(Const.Message.Message, "Hello World!")
                        .property(Const.Room.RoomId, "1234")
                        .property(Const.Message.Sender, "ryun")
                        .property(Const.Message.Event, Const.Event.onMessage);
                wsession.pathBy("/notifies/echoBot/" + notifyId).refTo("message", "/rooms/1234/messages/testMessage");
                return null;
            }
        });



        TalkResponse fakeResponse = TalkResponseBuilder.create().newInner().property("notifyId", notifyId).build();

        Bot bot = new Bot("echoBot", rsession, NewClient.create());
        bot.onMessage(fakeResponse);


        Thread.sleep(100);
        Iterator<String> iter = rsession.pathBy("/rooms/1234/messages/").childrenNames().iterator();

        String echoMessage = null;
        while(iter.hasNext()){
            echoMessage = iter.next();
            if(echoMessage != "testMessage")
                break;
        }

        ReadNode responseMessage = rsession.pathBy("/rooms/1234/messages/" + echoMessage);

        assertEquals("Hello World!", responseMessage.property(Const.Message.Message).stringValue());
        assertEquals("echoBot", responseMessage.property(Const.Message.Sender).stringValue());
        aradon.getServiceContext().removeAttribute(RepositoryEntry.EntryName);
        aradon.getServiceContext().removeAttribute(RhinoEntry.EntryName);
        aradon.stop();

    }

    @Override
    public void tearDown() throws Exception {
        rentry.shutdown();
        rengine.stopForTest();
        super.tearDown();
    }
}
