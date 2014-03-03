package net.ion.talk.handler;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.talk.handler.craken.TestNotifySendHandler;
import net.ion.talk.handler.craken.TestTalkMessageHandler;
import net.ion.talk.handler.craken.TestUserInAndOutRoomHandler;
import net.ion.talk.handler.engine.TestServerHandler;
import net.ion.talk.handler.engine.TestUserConnectionHandler;
import net.ion.talk.handler.engine.TestWebSocketTalkMessageHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 24.
 * Time: 오후 6:29
 * To change this template use File | Settings | File Templates.
 */
public class TestAllHandler extends TestCase{

    public static TestSuite suite(){
        TestSuite result = new TestSuite() ;

        //craken
        result.addTestSuite(TestNotifySendHandler.class);
        result.addTestSuite(TestUserInAndOutRoomHandler.class);
        result.addTestSuite(TestTalkMessageHandler.class);

        //engine
        result.addTestSuite(TestServerHandler.class);
        result.addTestSuite(TestUserConnectionHandler.class);
        result.addTestSuite(TestWebSocketTalkMessageHandler.class);


        return result ;
    }
}
