package net.ion.talk.deploy;

import net.ion.craken.node.ReadSession;
import net.ion.framework.util.InfinityThread;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BotManager;
import net.ion.talk.bot.EchoBot;
import net.ion.talk.handler.craken.NotificationListener;
import net.ion.talk.handler.craken.NotifyStrategy;
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

    public void testRunInfinite() throws Exception {
        tserver.addTalkHander(new UserConnectionHandler())
                .addTalkHander(new WebSocketMessageHandler());

        tserver.cbuilder().build();
        tserver.startRadon();

        ReadSession rsession = tserver.readSession();

        tserver.mockClient();

        rsession.workspace().cddm().add(new UserInAndOutRoomHandler());
        rsession.workspace().cddm().add(new TalkMessageHandler());
        rsession.workspace().addListener(new NotificationListener(new AccountManager(tserver.talkEngine(), NotifyStrategy.createSender(rsession)))) ;


        BotManager botManager = tserver.talkEngine().getServiceContext().getAttributeObject(BotManager.class.getCanonicalName(), BotManager.class);
        botManager.registerBot(new EchoBot());
        
//        new InfinityThread().startNJoin();
    }

}
