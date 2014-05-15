package net.ion.talk.handler.craken;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.ReadChildrenEach;
import net.ion.craken.node.crud.ReadChildrenIterator;
import net.ion.craken.tree.PropertyId;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.bot.TestCrakenBase;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 3. Time: 오후 6:26 To change this template use File | Settings | File Templates.
 */
public class TestUserInAndOutRoomHandler extends TestCrakenBase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		rsession.workspace().cddm().add(new UserInAndOutRoomHandler()).add(new TalkMessageHandler(NewClient.create()));

		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/1/members/ryun").refTo(Const.Message.Sender, "/users/alex");
				return null;
			}
		});
		rsession.tran(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/1/members/alex").refTo(Const.Message.Sender, "/users/ryun");
				return null;
			}
		});

        Thread.sleep(1000);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws Exception {
		assertEquals(1, rsession.ghostBy("/notifies/ryun").children().toList().size());
		assertEquals(0, rsession.ghostBy("/notifies/alex").children().toList().size());
	}
	
	public void testUserInAndOut() throws Exception {
		assertEquals(2, rsession.pathBy("/rooms/1/messages/").children().toList().size());
		rsession.pathBy("/rooms/1/messages/").children().eachNode(new ReadChildrenEach<Void>() {
			@Override
			public Void handle(ReadChildrenIterator iter) {
				while(iter.hasNext()){
					ReadNode next = iter.next();
					assertTrue(next.property(Message.ExclusiveSender).asBoolean()) ;
					assertTrue(next.propertyId(PropertyId.refer("sender")).asString().startsWith("/users/")) ;
				}
				return null;
			}
		}) ;
	}


}
