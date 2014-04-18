package net.ion.talk.handler.craken;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.TestCrakenBase;
import net.ion.talk.bean.Const;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 3. Time: 오후 6:26 To change this template use File | Settings | File Templates.
 */
public class TestUserInAndOutRoomHandler extends TestCrakenBase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		rsession.workspace().cddm().add(new UserInAndOutRoomHandler());

		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/1/members/ryun").property(Const.Message.Sender, "alex");
				return null;
			}
		});
		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/1/members/alex").property(Const.Message.Sender, "ryun");
				return null;
			}
		});

        Thread.sleep(1000);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUserInAndOut() throws Exception {

		assertEquals(2, rsession.pathBy("/rooms/1/messages/").children().toList().size());

		for (ReadNode node : rsession.pathBy("/rooms/1/messages/").children().toList()) {

			final String user = node.property("sender").stringValue();
			rsession.tranSync(new TransactionJob<Object>() {
				@Override
				public Object handle(WriteSession wsession) throws Exception {
					wsession.pathBy("/rooms/1/members/" + user).removeSelf();
					return null;
				}
			});
		}

		assertEquals(0, rsession.pathBy("/rooms/1/members/").childrenNames().size());
	}


}
