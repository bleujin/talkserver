package net.ion.talk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.craken.aradon.NodeLet;
import net.ion.craken.aradon.UploadLet;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.Radon;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.filter.CrakenVerifier;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.let.EmbedBotLet;
import net.ion.talk.let.LoginLet;
import net.ion.talk.let.ResourceLet;
import net.ion.talk.let.SMSAuthLet;
import net.ion.talk.let.ScriptEditLet;
import net.ion.talk.let.ScriptExecLet;
import net.ion.talk.script.TalkScript;

public class ToonServer {

	public static ToonServer testWithLoginLet() throws Exception {
		return new ToonServer().init();
	}

	private enum Status {
		INITED, READY, STARTED, STOPED ;
	}
	
	private RepositoryEntry repoEntry;
	private Aradon aradon;
	private Radon radon;
	private ConfigurationBuilder cbuilder;

	private AtomicReference<Status> status = new AtomicReference<ToonServer.Status>(Status.STOPED) ;
	
	private ToonServer init() throws Exception {
		this.repoEntry = RepositoryEntry.test();
		CrakenVerifier verifier = CrakenVerifier.test(repoEntry.login());
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(10) ;
		
		TalkScript tscript = TalkScript.create(repoEntry.login(), ses).readDir(new File("./script"), true) ;

		this.cbuilder = ConfigurationBuilder.newBuilder()	
				.aradon()
					.addAttribute(RepositoryEntry.EntryName, repoEntry)
					.addAttribute(TalkScript.class.getCanonicalName(), tscript)
					.addAttribute(ScheduledExecutorService.class.getCanonicalName(), ses)
				.sections()
					.restSection("auth").addPreFilter(new ChallengeAuthenticator("users", verifier))
						.path("login").addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
						
					.restSection("register")
						.path("smsAuth").addUrlPattern("/SMSAuth").matchMode(IMatchMode.STARTWITH).handler(SMSAuthLet.class)
						
					.restSection("script")
						.path("script").addUrlPattern("/").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ScriptEditLet.class)
						
					.restSection("execute")
						.path("execute").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
						
					.restSection("resource")
						.path("resource").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
						
					.restSection("bot")
						.path("bot").addUrlPattern("/{botId}").matchMode(IMatchMode.STARTWITH).handler(EmbedBotLet.class)

						
					.restSection("admin").addAttribute("baseDir", "./resource/template").addAttribute("repository", repoEntry.repository())
						.path("node").addUrlPattern("/repository/{workspace}/{renderType}").matchMode(IMatchMode.STARTWITH).handler(NodeLet.class)
						.path("template").addUrlPattern("/template").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
						.path("upload").addUrlPattern("/upload").matchMode(IMatchMode.STARTWITH).handler(UploadLet.class).toBuilder() ;
						
//					.restSection("websocket").addAttribute(TalkHandlerGroup.class.getCanonicalName(), talkHandlerGroup)
//						.wspath("websocket").addUrlPattern("/{id}/{accessToken}").handler(TalkEngine.class).toBuilder();

		status.set(Status.INITED);
		return this;
	}

	public static long GMTTime() {
		return GregorianCalendar.getInstance().getTime().getTime();
	}

	public ToonServer ready() throws Exception {
		this.aradon = Aradon.create(cbuilder.build());
		this.radon = aradon.toRadon(9000);
		
		TalkEngine talkEngine = new TalkEngine(aradon.getServiceContext()) ;
		talkEngine.startEngine() ;
		radon.add("/websocket/{id}/{accessToken}", talkEngine) ;
		
		status.set(Status.READY);
		return this;
	}


	public ToonServer startRadon() throws Exception {
		if (status.get() != Status.READY) throw new IllegalStateException("current status is " + status.get()) ;
		
		radon.start().get();
		status.set(Status.STARTED);
		return this;
	}

	public void stop() throws InterruptedException, ExecutionException {
		repoEntry.shutdown();
		if (aradon != null) aradon.stop();
		if (radon != null) radon.stop().get();
		
		status.set(Status.STOPED);
	}

	public Aradon aradon() {
		checkStarted();
		return aradon;
	}

	private void checkStarted() {
		if (aradon == null)
			throw new IllegalStateException("Aradon not started");
	}


	public ReadSession readSession() throws IOException {
		checkStarted();
		return repoEntry.login();
	}

	public TalkEngine talkEngine() {
		checkStarted();
		return aradon.getServiceContext().getAttributeObject(TalkEngine.class.getCanonicalName(), TalkEngine.class);
	}

	public ToonServer addAttribute(Object value) {
		aradon.getServiceContext().putAttribute(value.getClass().getCanonicalName(), value);
		return this;
	}

	public <T> T getAttribute(String key, Class<T> clz) {
		return aradon.getServiceContext().getAttributeObject(key, clz);
	}

	public String getHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}
}
