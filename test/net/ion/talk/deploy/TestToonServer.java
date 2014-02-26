package net.ion.talk.deploy;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.aradon.AradonHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketTextListener;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkMessage;
import net.ion.talk.let.*;
import net.ion.talk.util.NetworkUtil;

import org.restlet.data.Method;

import java.util.concurrent.CountDownLatch;

public class TestToonServer extends TestCase {


    private TalkEngine tengine;


    public void testRun() throws Exception {
		RepositoryEntry repository = RepositoryEntry.test();
        ReadSession session = repository.login();
        CrakenVerifier verifier = CrakenVerifier.test(session);
        RhinoEntry rengine = RhinoEntry.test() ;

		Configuration config = ConfigurationBuilder.newBuilder().aradon()
			.addAttribute(RepositoryEntry.EntryName, repository)
			.addAttribute(RhinoEntry.EntryName, rengine)
			.sections().restSection("script")
				.path("jscript").addUrlPattern("/{name}.{format}").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
                .restSection("auth")
                .addPreFilter(new ChallengeAuthenticator("users", verifier))
                .path("login")
                .addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
			.build();


		Aradon aradon = Aradon.create(config);
		tengine = TalkEngine.create(aradon) ;
		tengine.registerHandler(new DebugTalkHandler()) ;
		
		AradonHandler ahandler = AradonHandler.create(aradon);

		Radon radon = RadonConfiguration.newBuilder(9000)
                .add("/script/*", ahandler)
                .add("/auth/*", ahandler)
			.add("/websocket/{id}", tengine)
			.add("/resource/*", new ResourceFileHandler("./resource/"))
			.createRadon() ;
		
		radon.start().get() ;

//        new InfinityThread().startNJoin();

		radon.stop().get() ;
	}
	
	public void xtestWebSocket() throws Exception {
		NewClient client = NewClient.create();
		final CountDownLatch cd = new CountDownLatch(1) ;
		WebSocket ws = client.createWebSocket(NetworkUtil.getHostAddressWithProtocol("ws") + ":9000/websocket/bleujin", new WebSocketTextListener() {
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
				Debug.line(received) ;
				cd.countDown() ;
			}
			@Override
			public void onFragment(String arg0, boolean arg1) {
			}
		});
		
		String msg = TalkMessage.fromScript("session.pathBy('/bleujin').toRows('name, age').toString();").toPlainMessage() ;
		Debug.line(msg) ;
		ws.sendTextMessage(msg) ;
		cd.await() ;
		
		client.close();
	}


    @Override
    public void tearDown() throws Exception {
        tengine.stopForTest();
        super.tearDown();
    }
}
