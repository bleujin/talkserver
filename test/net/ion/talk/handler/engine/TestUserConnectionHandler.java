package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;
import net.ion.talk.bean.Const;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 3. Time: 오후 5:15 To change this template use File | Settings | File Templates.
 */
public class TestUserConnectionHandler extends TestCase {

	private WebSocketConnection ryun = FakeWebSocketConnection.create("ryun");
	private ReadSession rsession;
	private TalkEngine engine;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		engine = TalkEngine.testCreate().registerHandler(new UserConnectionHandler()).startEngine();
		rsession = engine.readSession();

		rsession.tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				ryun.data("accessToken", "testToken");
				wsession.pathBy("/users/ryun").property(Const.User.AccessToken, "testToken");
				return null;
			}
		});

	}

	@Override
	public void tearDown() throws Exception {
		engine.stopEngine();
		super.tearDown();
	}

	public void testUserInAndOut() throws Exception {

        engine.onOpen(ryun);
        assertTrue(rsession.exists("/connections/" + ryun.getString("id")));
        assertEquals(rsession.workspace().repository().memberId(), rsession.pathBy("/connections/" + ryun.getString("id")).property("delegateServer").stringValue());

        engine.onClose(ryun);
        assertFalse(rsession.exists("/connections/" + ryun.getString("id")));
    }

}
