package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
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
public class TestChatBot extends TestCase{

    private RepositoryEntry rentry;
    private ReadSession rsession;
    private RhinoEntry rengine;
    private Aradon aradon;
    private ChatBot chatBot;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rentry = RepositoryEntry.test();
        rengine = RhinoEntry.test();
        rengine.startForTest();
        rsession = rentry.login();
        chatBot = new ChatBot(rsession);


        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/members/ryun");
                wsession.pathBy("/rooms/test/members/alex");
                wsession.pathBy("/rooms/test/members/chatBot").property(Const.Bot.isSyncBot, "true");
                return null;
            }
        });


        BotManager botManager = BotManager.create(rsession);
        botManager.registerBot(new EchoBot(rsession));

        aradon = AradonTester.create().register("", "/bot",  EmbedBotLet.class).getAradon().startServer(9000);
        aradon.getServiceContext().putAttribute(RepositoryEntry.EntryName, rentry);
        aradon.getServiceContext().putAttribute(RhinoEntry.EntryName, rengine);
        aradon.getServiceContext().putAttribute(BotManager.class.getCanonicalName(), botManager);
    }

    public void testOnFilter() throws Exception {


        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/messages/testMessage")
                        .property(Const.Message.Message, "happy")
                        .property(Const.Message.ClientScript, "client.rooms().message(args.message);")
                        .property(Const.Message.Sender, "ryun")
                        .property(Const.Message.Event, Const.Event.onMessage);
                return null;
            }
        });

        chatBot.onFilter("test", "ryun", "happy", "testMessage");

        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertTrue(StringUtil.startsWith(messageNode.property(Const.Message.ClientScript).stringValue(), "client.character"));
        assertEquals(1, rsession.pathBy("/rooms/test/messages").childrenNames().size());
    }

    public void testHappy() throws Exception{

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/messages/testMessage")
                        .property(Const.Message.Message, "happy")
                        .property(Const.Message.ClientScript, "client.rooms().message(args.message);")
                        .property(Const.Message.Sender, "ryun")
                        .property(Const.Message.Event, Const.Event.onMessage);
                return null;
            }
        });

        chatBot.onFilter("test", "ryun", "happy", "testMessage");

        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertTrue(messageNode.property(Const.Message.ClientScript).stringValue().contains("motion(\"0\")"));

    }


    public void testAngry() throws Exception{

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/messages/testMessage")
                        .property(Const.Message.Message, "angry")
                        .property(Const.Message.ClientScript, "client.rooms().message(args.message);")
                        .property(Const.Message.Sender, "ryun")
                        .property(Const.Message.Event, Const.Event.onMessage);
                return null;
            }
        });

        chatBot.onFilter("test", "ryun", "angry", "testMessage");

        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertTrue(messageNode.property(Const.Message.ClientScript).stringValue().contains("motion(\"3\")"));

    }
    public void testSad() throws Exception{

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/messages/testMessage")
                        .property(Const.Message.Message, "sad")
                        .property(Const.Message.ClientScript, "client.rooms().message(args.message);")
                        .property(Const.Message.Sender, "ryun")
                        .property(Const.Message.Event, Const.Event.onMessage);
                return null;
            }
        });

        chatBot.onFilter("test", "ryun", "sad", "testMessage");

        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertTrue(messageNode.property(Const.Message.ClientScript).stringValue().contains("motion(\"1\")"));

    }


    @Override
    public void tearDown() throws Exception {
        aradon.stop();
        super.tearDown();
    }

}
