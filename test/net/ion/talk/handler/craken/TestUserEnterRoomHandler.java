package net.ion.talk.handler.craken;

import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 6:26
 * To change this template use File | Settings | File Templates.
 */
public class TestUserEnterRoomHandler extends TestCrakenHandlerBase{

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rsession.workspace().cddm().add(new UserInAndOutRoomHandler());
    }

    public void testUserIn() throws Exception {
        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/1/members/ryun");
                return null;
            }
        });

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/1/members/alex");
                return null;
            }
        });

        Thread.sleep(100);
        assertEquals(1, rsession.pathBy("/notifies/alex").children().toList().size());
        assertEquals(2, rsession.pathBy("/notifies/ryun").children().toList().size());

    }

}
