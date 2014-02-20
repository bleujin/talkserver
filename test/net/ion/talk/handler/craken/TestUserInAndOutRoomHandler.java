package net.ion.talk.handler.craken;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 6:26
 * To change this template use File | Settings | File Templates.
 */
public class TestUserInAndOutRoomHandler extends TestCrakenHandlerBase{

    private List<String> users;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rsession.workspace().cddm().add(new UserInAndOutRoomHandler());

        users = ListUtil.syncList("ryun", "alex");

        for(final String user : users){
            rsession.tranSync(new TransactionJob<Object>() {
                @Override
                public Object handle(WriteSession wsession) throws Exception {
                    wsession.pathBy("/rooms/1/members/"+user);
                    return null;
                }
            });
        }
//
//        Thread.sleep(100);
    }
    
    @Override
    public void tearDown() throws Exception {
    	super.tearDown();
    }

    public void testUserIn() {

        assertEquals(2, rsession.pathBy("/rooms/1/messages/").children().toList().size());

        for(ReadNode node : rsession.pathBy("/rooms/1/messages/").children().toList()){
            String user = node.ref("sender").fqn().name();
            users.remove(user);
        }

        assertEquals(0, users.size());

    }

    public void testUserOut() throws Exception {
        for(final String user : users){
            rsession.tranSync(new TransactionJob<Object>() {
                @Override
                public Object handle(WriteSession wsession) throws Exception {
                    wsession.pathBy("/rooms/1/members/"+user).removeSelf();
                    return null;
                }
            });
        }
        assertEquals(4, rsession.pathBy("/rooms/1/messages/").children().toList().size());
        assertFalse(rsession.pathBy("/rooms/1/members").children().hasNext());
    }

}
