package net.ion.talk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.Radon;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.let.CrakenVerifier;
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


	private ToonServer init() throws Exception {
		final RepositoryEntry rentry = RepositoryEntry.test();
		this.session = rentry.login("test") ;
		this.verifier = CrakenVerifier.test(session);
		
		TalkHandlerGroup tg = TalkHandlerGroup.create();
		tg.addHandler(ServerHandler.test()) ;
		
		Configuration config = ConfigurationBuilder.newBuilder().aradon()
		.addAttribute(RepositoryEntry.EntryName, rentry)
		.sections().restSection("auth")
			.addPreFilter(new ChallengeAuthenticator("users", verifier))
				.path("login")
				.addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
			.restSection("websocket")
				.addAttribute(TalkHandlerGroup.class.getCanonicalName(), tg)
				.wspath("websocket")
				.addUrlPattern("/{id}/{token}").handler(TalkEngine.class) 
				
		.build();


		this.aradon = Aradon.create(config);

		return this;
	}
	
	public ToonServer start() throws InterruptedException, ExecutionException, FileNotFoundException{
		this.radon = aradon.toRadon(9000) ;
		radon.start().get() ;
		return this ;
	}

	public void stop() throws InterruptedException, ExecutionException {
		if (radon != null) 
			radon.stop().get() ;
	}

	public Aradon aradon() {
		return aradon ;
	}

	@Deprecated
	public CrakenVerifier verifier() {
		return verifier;
	}

	public ReadSession readSession() {
		return session;
	}

	public TalkEngine talkEngine() {
		return aradon.getServiceContext().getAttributeObject(TalkEngine.class.getCanonicalName(), TalkEngine.class) ;
	}

}
