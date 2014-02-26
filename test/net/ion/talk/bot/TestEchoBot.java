package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.ObjectId;
import net.ion.radon.core.context.OnEventObject;
import net.ion.talk.bean.Const;

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
        echoBot = new EchoBot();
    }


    public void testOnEnter() throws Exception {
        String script = echoBot.onEnter("test", "ryun");
        rengine.executeScript(rsession, new ObjectId().toString(), script, null);
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().next();
        assertEquals("Hello! ryun", messageNode.property(Const.Message.Message).stringValue());
    }

    public void testOnExit() throws Exception {
        String script = echoBot.onExit("test", "ryun");
        rengine.executeScript(rsession, new ObjectId().toString(), script, null);
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().next();
        assertEquals("Bye! ryun", messageNode.property(Const.Message.Message).stringValue());

    }

    public void testOnMessage() throws Exception {
        String script = echoBot.onMessage("test", "ryun", "Everybody Hello!");
        rengine.executeScript(rsession, new ObjectId().toString(), script, null);
        ReadNode messageNode = rsession.pathBy("/rooms/test/messages/").children().next();
        assertEquals("Everybody Hello!", messageNode.property(Const.Message.Message).stringValue());
    }

    @Override
    public void tearDown() throws Exception {
        rentry.shutdown();
        rengine.stopForTest();
        super.tearDown();
    }
}
