package net.ion.talk.deploy;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketTextListener;
import net.ion.talk.ParameterMap;
import net.ion.talk.TalkMessage;
import net.ion.talk.ToonServer;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bot.BBot;
import net.ion.talk.bot.BotManager;
import net.ion.talk.bot.ChatBot;
import net.ion.talk.bot.EchoBot;
import net.ion.talk.handler.craken.NotificationListener;
import net.ion.talk.handler.craken.NotifyStrategy;
import net.ion.talk.handler.craken.TalkMessageHandler;
import net.ion.talk.handler.craken.UserInAndOutRoomHandler;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.handler.engine.UserConnectionHandler;
import net.ion.talk.handler.engine.WebSocketScriptHandler;
import net.ion.talk.util.NetworkUtil;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 3. Time: 오후 3:21 To change this template use File | Settings | File Templates.
 */
public class TestToonServerNew extends TestCase {

	public void testRunInfinite() throws Exception {
		String filePath = "./resource/craken";
		FileUtil.deleteDirectory(new File(filePath));
		
		RepositoryEntry rentry = RepositoryEntry.testSoloFile(filePath) ;
		ScheduledExecutorService worker = Executors.newScheduledThreadPool(10) ;

		ToonServer tserver = ToonServer.testCreate(rentry, worker);
		
		tserver.ready();
		tserver.startRadon();
		
		tserver.talkEngine().registerHandler(new UserConnectionHandler()).registerHandler(ServerHandler.test()).registerHandler(new WebSocketScriptHandler()) ;

		NewClient nc = tserver.getAttribute(NewClient.class.getCanonicalName(), NewClient.class);
		BotManager botManager = tserver.getAttribute(BotManager.class.getCanonicalName(), BotManager.class);
		ReadSession rsession = tserver.readSession();
		
		
		rsession.workspace().cddm().add(new UserInAndOutRoomHandler());
		rsession.workspace().cddm().add(new TalkMessageHandler(nc));
		rsession.workspace().addListener(new NotificationListener(AccountManager.create(tserver.talkEngine(), NotifyStrategy.createPusher(worker, rsession))));


        botManager.registerBot(new EchoBot(tserver.readSession(), worker));
        botManager.registerBot(new BBot(tserver.readSession(), worker));
        botManager.registerBot(new ChatBot(tserver.readSession()));

        new InfinityThread().startNJoin();
    }

	
	public void xtestWebSocket() throws Exception {
		NewClient client = NewClient.create();
		final CountDownLatch cd = new CountDownLatch(1);
		WebSocket ws = client.createWebSocket(NetworkUtil.wsAddress(9000, "/websocket/bleujin"), new WebSocketTextListener() {
			@Override
			public void onOpen(WebSocket arg0) {
			}

			@Override
			public void onError(Throwable arg0) {
			}

			@Override
			public void onClose(WebSocket arg0) {
			}

			@Override
			public void onMessage(String received) {
				Debug.line(received);
				cd.countDown();
			}

			@Override
			public void onFragment(String arg0, boolean arg1) {
			}
		});

		String msg = TalkMessage.fromScript("1234", "/test/hello", ParameterMap.BLANK).toPlainMessage();
		Debug.line(msg);
		ws.sendTextMessage(msg);
		cd.await();

		client.close();
	}

	
}
