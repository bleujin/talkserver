package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
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
        engine = TalkEngine.test().registerHandler(new UserConnectionHandler()).startForTest();
        rsession = engine.readSession();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/users/ryun");
                return null;
            }
        });

    }

    @Override
    public void tearDown() throws Exception {
        engine.stopForTest();
        super.tearDown();
    }

    public void testUserInAndOut() throws Exception {

        engine.onOpen(ryun);
        assertTrue(rsession.exists("/connections/"+ryun.getString("id")));
        assertEquals(rsession.workspace().repository().memberId(), rsession.pathBy("/users/"+ryun.getString("id")).property("delegateServer").stringValue());

        engine.onClose(ryun);
        assertFalse(rsession.exists("/connections/"+ryun.getString("id")));
    }

}
