package net.ion.talk.deploy;

import java.io.File;
import java.io.InputStream;
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
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.restlet.FileMetaType;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketTextListener;
import net.ion.talk.ParameterMap;
import net.ion.talk.TalkMessage;
import net.ion.talk.ToonServer;
import net.ion.talk.bean.Const.User;
import net.ion.talk.util.NetworkUtil;


public class TestToonServerNew extends TestCase {

	public void testRunInfinite() throws Exception {
		String filePath = "./resource/craken";
		FileUtil.deleteDirectory(new File(filePath));
		
	//	RepositoryEntry rentry = RepositoryEntry.testSoloFile(filePath) ;
		RepositoryEntry rentry = RepositoryEntry.test() ;
		ScheduledExecutorService worker = Executors.newScheduledThreadPool(10) ;

		final ToonServer tserver = ToonServer.testCreate(rentry, worker);
		tserver.ready().startRadon();
		
		
		ReadSession rsession = tserver.talkEngine().readSession();
		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/hero@i-on.net").property(User.UserId, "hero@i-on.net").property(User.Password, "1").property(User.NickName, "hero").property(User.StateMessage, "-_-;").property(User.Phone, "1042216492") ;
				wsession.pathBy("/users/bleujin@i-on.net").property(User.UserId, "bleujin@i-on.net").property(User.Password, "1").property(User.NickName, "bleujin").property(User.StateMessage, "-_-a").property(User.Phone, "1042216492") ;
				wsession.pathBy("/users/airkjh@i-on.net").property(User.UserId, "airkjh@i-on.net").property(User.Password, "1").property(User.NickName, "airkjh").property(User.StateMessage, "-_-a").property(User.Phone, "1042216491") ;
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
				arg0.printStackTrace();
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

		String msg = TalkMessage.fromTest("1234", "/test/hello", ParameterMap.BLANK).toPlainMessage();
		Debug.line(msg);
		ws.sendTextMessage(msg);
		cd.await();

		client.close();
	}

	
}
