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
import net.ion.nradon.Radon;
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
public class TestBBot extends TestCase{

    private RepositoryEntry rentry;
    private ReadSession rsession;
    private RhinoEntry rengine;
    private Aradon aradon;
    private BBot bBot;
	private Radon radon;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rentry = RepositoryEntry.test();
        rengine = RhinoEntry.test();
        rengine.startForTest();
        rsession = rentry.login();
        bBot = new BBot(rsession);


        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/members/ryun");
                wsession.pathBy("/rooms/test/members/alex");
                wsession.pathBy("/rooms/test/members/bBot");
                return null;
            }
        });


        BotManager botManager = BotManager.create(rsession);
        botManager.registerBot(bBot);

        aradon = AradonTester.create().register("", "/bot",  EmbedBotLet.class).getAradon() ;
        this.radon = aradon.toRadon(9000).start().get();
        aradon.getServiceContext().putAttribute(RepositoryEntry.EntryName, rentry);
        aradon.getServiceContext().putAttribute(RhinoEntry.EntryName, rengine);
        aradon.getServiceContext().putAttribute(BotManager.class.getCanonicalName(), botManager);
    }

    @Override
    public void tearDown() throws Exception {
    	Debug.line();
        radon.stop().get() ;
        super.tearDown();
    }


    public void testOnEnter() throws Exception {
        bBot.onEnter("test", "bBot");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        Debug.line("안녕하세요. B@bot 입니다! 도움이 필요하다면 \"B@메시지\"이라고 입력해주세요 :) ex) B@도움말, B@식당, B@월차", messageNode.property(Const.Message.Message).stringValue());
    }

    public void testOnExit() throws Exception {
        bBot.onExit("test", "bBot");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertEquals("나중에 또 봐요 ~", messageNode.property(Const.Message.Message).stringValue());

    }

    public void testHelpWithOutAccount() throws Exception {
        bBot.onMessage("test", "ryun", "B@도움말");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertEquals("계정 정보가 없습니다. \"/register 이메일 비밀번호\"를 이용하여 계정정보를 입력해주세요.", messageNode.property(Const.Message.Message).stringValue());
    }

    public void testRegisterAccount() throws Exception {
        bBot.onMessage("test", "ryun", "/register ryun@i-on.net ryun");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertEquals("ryun@i-on.net 계정이 정상적으로 등록되었습니다!", messageNode.property(Const.Message.Message).stringValue());
    }

    public void testHelpWithAccount() throws Exception {
        bBot.onMessage("test", "ryun", "/register ryun@i-on.net ryun");
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode() ;
        assertEquals("ryun@i-on.net 계정이 정상적으로 등록되었습니다!", messageNode.property(Const.Message.Message).stringValue());
        removeRecentMessage();

        bBot.onMessage("test", "ryun", "B@도움말");
        messageNode = rsession.pathBy("/rooms/test/messages/").children().firstNode();
        assertEquals("명령을 보냈습니다!", messageNode.property(Const.Message.Message).stringValue());
        removeRecentMessage();
    }

    private void removeRecentMessage() throws Exception {
        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/messages/").children().firstNode().removeSelf();
                return null;
            }
        });
    }

    public ReadNode readMessage() {
        Iterator<String> iter = rsession.pathBy("/rooms/1234/messages/").childrenNames().iterator();

        String echoMessage = null;
        while(iter.hasNext()){
            echoMessage = iter.next();
            if(!StringUtil.startsWith(echoMessage, "testMessage"))
                break;
        }

        return rsession.pathBy("/rooms/1234/messages/" + echoMessage);
    }





}
