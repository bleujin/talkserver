package net.ion.talk;

import net.ion.craken.node.ReadSession;
import net.ion.framework.util.InfinityThread;
import net.ion.talk.handler.craken.NotificationSendHandler;
import net.ion.talk.handler.craken.TalkMessageHandler;
import net.ion.talk.handler.craken.UserInAndOutRoomHandler;
import net.ion.talk.handler.engine.UserConnectionHandler;
import net.ion.talk.handler.engine.WebSocketMessageHandler;
import net.ion.talk.let.TestBaseLet;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 3:21
 * To change this template use File | Settings | File Templates.
 */
public class TestToonServerNew extends TestBaseLet{

    NotificationSendHandler notiHandler = new NotificationSendHandler();

    public void testRunInfinite() throws Exception {
        tserver.addTalkHander(new UserConnectionHandler())
                .addTalkHander(new WebSocketMessageHandler())
                .addTalkHander(notiHandler);

        tserver.cbuilder().build();
        tserver.startRadon();

        ReadSession rsession = tserver.readSession();

        tserver.verifier().addUser("ryun", "1234");

        tserver.mockClient();

        rsession.workspace().cddm().add(new UserInAndOutRoomHandler());
        rsession.workspace().cddm().add(new TalkMessageHandler());
        rsession.workspace().cddm().add(notiHandler);


        new InfinityThread().startNJoin();
    }

}
