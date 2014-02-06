package net.ion.talk;

import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.InfinityThread;
import net.ion.talk.handler.craken.UserEnterRoomHandler;
import net.ion.talk.handler.craken.UserUserMessageHandler;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.handler.engine.UserConnectionHandler;
import net.ion.talk.handler.engine.WebSocketTalkMessageHandler;
import net.ion.talk.let.TestBaseLet;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 3:21
 * To change this template use File | Settings | File Templates.
 */
public class TestToonServerNew extends TestBaseLet{



    public void testRunInfinite() throws Exception {
        tserver.addTalkHander(new UserConnectionHandler())
                .addTalkHander(new WebSocketTalkMessageHandler());
        tserver.cbuilder().build();
        tserver.startRadon();
        ReadSession rsession = tserver.readSession();

        tserver.verifier().addUser("ryun", "1234");

        tserver.mockClient();

        rsession.workspace().cddm().add(new UserEnterRoomHandler());
        rsession.workspace().cddm().add(new UserUserMessageHandler());

        new InfinityThread().startNJoin();
    }
}
