package net.ion.talk.deploy;

import net.ion.craken.node.ReadSession;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BBot;
import net.ion.talk.bot.BotManager;
import net.ion.talk.bot.ChatBot;
import net.ion.talk.bot.EchoBot;
import net.ion.talk.handler.craken.NotificationListener;
import net.ion.talk.handler.craken.NotifyStrategy;
import net.ion.talk.handler.craken.TalkMessageHandler;
import net.ion.talk.handler.craken.UserInAndOutRoomHandler;
import net.ion.talk.handler.engine.InitScriptHandler;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.handler.engine.UserConnectionHandler;
import net.ion.talk.handler.engine.WebSocketMessageHandler;
import net.ion.talk.let.TestBaseLet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 3. Time: 오후 3:21 To change this template use File | Settings | File Templates.
 */
public class TestToonServerNew extends TestBaseLet {

	public void testRunInfinite() throws Exception {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

		tserver.addTalkHander(new UserConnectionHandler()).addTalkHander(ServerHandler.test()).addTalkHander(new WebSocketMessageHandler()).addTalkHander(new InitScriptHandler());

		tserver.cbuilder().build();
		tserver.startRadon();

		ReadSession rsession = tserver.readSession();

		tserver.mockClient();



		rsession.workspace().cddm().add(new UserInAndOutRoomHandler());
		rsession.workspace().cddm().add(new TalkMessageHandler(tserver.mockClient().real()));
		rsession.workspace().addListener(new NotificationListener(new AccountManager(tserver.talkEngine(), NotifyStrategy.createSender(ses, rsession))));

        BotManager botManager = tserver.talkEngine().getServiceContext().getAttributeObject(BotManager.class.getCanonicalName(), BotManager.class);

        botManager.registerBot(new EchoBot(tserver.readSession(), ses));
        botManager.registerBot(new BBot(tserver.readSession(), ses));
        botManager.registerBot(new ChatBot(tserver.readSession()));

        new InfinityThread().startNJoin();
    }

}
