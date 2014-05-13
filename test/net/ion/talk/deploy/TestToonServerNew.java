package net.ion.talk.deploy;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
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
import net.ion.talk.bean.Const.User;
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
//		RepositoryEntry rentry = RepositoryEntry.test() ;
		ScheduledExecutorService worker = Executors.newScheduledThreadPool(10) ;

		final ToonServer tserver = ToonServer.testCreate(rentry, worker);
		tserver.ready().startRadon();
		
		ReadSession rsession = tserver.talkEngine().readSession();
		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/hero@i-on.net").property(User.Password, "1").property(User.NickName, "hero").property(User.StateMessage, "-_-;").property(User.Phone, "1042216492") ;
				wsession.pathBy("/users/bleujin@i-on.net").property(User.Password, "1").property(User.NickName, "bleujin").property(User.StateMessage, "-_-a").property(User.Phone, "1042216492") ;
				
				wsession.pathBy("/rooms/roomroom/members/hero@i-on.net") ;
				return null;
			}
		});
        
        Runtime.getRuntime().addShutdownHook(new Thread(){
        	public void run(){
        		try {
					tserver.stop();
				} catch (Exception e) {
					e.printStackTrace();
				} 
        	}
        });
        
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
