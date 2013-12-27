package net.ion.talk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.Radon;
import net.ion.radon.client.AradonClient;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.let.CrakenVerifier;
import net.ion.talk.let.EchoHandler;
import net.ion.talk.let.LoginLet;
import net.ion.talk.let.ServerHandler;
import net.ion.talk.let.TalkHandlerGroup;

public class ToonServer {

	public static ToonServer testWithLoginLet() throws Exception {
		return new ToonServer().init();
	}

	private ReadSession session;
	private CrakenVerifier verifier;
	private Aradon aradon;
	private Radon radon;
	private ConfigurationBuilder cbuilder;
	private TalkHandlerGroup talkHandlerGroup;
	private MockClient mockClient;

	private ToonServer init() throws Exception {
		final RhinoEntry rengine = RhinoEntry.test() ;
		final RepositoryEntry rentry = RepositoryEntry.test();
		this.session = rentry.login() ;
		this.verifier = CrakenVerifier.test(session);
		
		this.talkHandlerGroup = TalkHandlerGroup.create();
		talkHandlerGroup.addHandler(ServerHandler.test()) ;
		
		this.cbuilder = ConfigurationBuilder.newBuilder().aradon()
		.addAttribute(RepositoryEntry.EntryName, rentry)
		.addAttribute(RhinoEntry.EntryName, rengine)
		.sections().restSection("auth")
			.addPreFilter(new ChallengeAuthenticator("users", verifier))
				.path("login")
				.addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
			.restSection("websocket")
				.addAttribute(TalkHandlerGroup.class.getCanonicalName(), talkHandlerGroup)
				.wspath("websocket")
				.addUrlPattern("/{id}/{token}").handler(TalkEngine.class).toBuilder() ;

		this.mockClient = MockClient.create(this) ;
		return this;
	}
	
	
	public ToonServer addTalkHander(TalkHandler thandler) {
		talkHandlerGroup.addHandler(thandler) ;
		return this;
	}
	
	public ConfigurationBuilder cbuilder() {
		return cbuilder;
	}

	
	public ToonServer startAradon() {
		this.aradon = Aradon.create(cbuilder.build());
		aradon.start() ;
		return this ;
	} 
	
	public ToonServer startRadon() throws InterruptedException, ExecutionException, FileNotFoundException{
		this.aradon = Aradon.create(cbuilder.build());
		this.radon = aradon.toRadon(9000) ;
		radon.start().get() ;
		return this ;
	}

	public void stop() throws InterruptedException, ExecutionException {
		mockClient.close() ;
		if (aradon != null) aradon.stop() ;
		if (radon != null) 
			radon.stop().get() ;
	}

	public Aradon aradon() {
		checkStarted() ;
		return aradon ;
	}

	private void checkStarted() {
		if (aradon == null)
			throw new IllegalStateException("not started") ;
	}

	@Deprecated
	public CrakenVerifier verifier() {
		return verifier;
	}

	public ReadSession readSession() {
		checkStarted() ;
		return session;
	}

	public TalkEngine talkEngine() {
		checkStarted() ;
		return aradon.getServiceContext().getAttributeObject(TalkEngine.class.getCanonicalName(), TalkEngine.class) ;
	}


	public MockClient mockClient() {
		return mockClient;
	}




}
