package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.FakeConnection;
import net.ion.talk.TalkEngine;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 5:15
 * To change this template use File | Settings | File Templates.
 */
public class TestUserConnectionHandler extends TestCase {


    private WebSocketConnection ryun = FakeConnection.create("ryun");
    private ReadSession rsession;
    private TalkEngine engine;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        engine = TalkEngine.test();
        rsession = engine.readSession();
        engine.registerHandler(new UserConnectionHandler(rsession));
    }

    @Override
    public void tearDown() throws Exception {
        engine.onStop();
        super.tearDown();
    }

    public void testUserInAndOut() throws Exception {

        engine.onOpen(ryun);
        ReadNode node = rsession.ghostBy("/users/"+ryun.getString("id")+"/connection");
        assertEquals(true, node.property("isConnected").value());
        assertEquals(rsession.workspace().repository().memberId(), node.property("server").stringValue());

        engine.onClose(ryun);
        assertEquals(false, node.property("isConnected").value());
        assertEquals("none", node.property("server").stringValue());
    }

}
