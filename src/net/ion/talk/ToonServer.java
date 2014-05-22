package net.ion.talk;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.craken.aradon.MonitorLet;
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
import net.ion.talk.filter.ToonAuthenticator;
import net.ion.talk.let.EmbedBotLet;
import net.ion.talk.let.LoginLet;
import net.ion.talk.let.PhoneAuthLet;
import net.ion.talk.let.ResourceLet;
import net.ion.talk.let.ScriptConfirmLet;
import net.ion.talk.let.ScriptDoLet;
import net.ion.talk.let.ScriptExecLet;
import net.ion.talk.let.UserLet;
import net.ion.talk.monitor.TalkMonitor;
import net.ion.talk.script.TalkScript;
import net.ion.talk.toonweb.ClientLet;
import net.ion.talk.toonweb.ReloadLet;
import net.ion.talk.toonweb.ToonWebResourceLet;

public class ToonServer {

	private enum Status {
		INITED, READY, STARTED, STOPED ;
	}
	
	private Aradon aradon;
	private Radon radon;

	private AtomicReference<Status> status = new AtomicReference<ToonServer.Status>(Status.STOPED) ;
	
	private RepositoryEntry repoEntry;
	private ScheduledExecutorService worker;
	private TalkEngine talkEngine;

	private ToonServer(RepositoryEntry rentry, ScheduledExecutorService worker){
		this.repoEntry = rentry ;
		this.worker = worker ;
	}

	public static ToonServer testCreate() throws Exception {
		return testCreate(RepositoryEntry.test(), Executors.newScheduledThreadPool(10)) ;
	}

	public static ToonServer testCreate(RepositoryEntry rentry, ScheduledExecutorService worker) throws Exception {
		return new ToonServer(rentry, worker).init();
	}

	
	
	private ToonServer init() throws Exception {
		CrakenVerifier verifier = CrakenVerifier.test(repoEntry.login());
		
		ConfigurationBuilder cbuilder = ConfigurationBuilder.newBuilder()	
				.aradon()
					.addAttribute(RepositoryEntry.EntryName, repoEntry)
					.addAttribute(ScheduledExecutorService.class.getCanonicalName(), worker)
				.sections()
					.restSection("auth").addPreFilter(new ChallengeAuthenticator("users", verifier))
						.path("login").addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
						
					.restSection("register")
						.path("user").addUrlPattern("/user/{email}").matchMode(IMatchMode.STARTWITH).handler(UserLet.class)
						.path("smsAuth").addUrlPattern("/SMSAuth").matchMode(EnumClass.IMatchMode.STARTWITH).handler(PhoneAuthLet.class)
						
					.restSection("script")
						.path("script").addUrlPattern("/").matchMode(EnumClass.IMatchMode.EQUALS).handler(ScriptConfirmLet.class)
						
					.restSection("execute")
						.path("execute").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
						
					.restSection("resource")
						.path("resource").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
						
					.restSection("bot")
						.path("bot").addUrlPattern("/{botId}").matchMode(IMatchMode.STARTWITH).handler(EmbedBotLet.class)
						
					.restSection("session")
						.addPreFilter(new ToonAuthenticator("user"))
						.path("client").addUrlPattern("/").handler(ClientLet.class)
						.path("reload").addUrlPattern("/reload").handler(ReloadLet.class)

					.restSection("toonweb")
						.path("toonweb").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ToonWebResourceLet.class)
						
					.restSection("admin").addAttribute("baseDir", "./resource/template")
						.path("node").addUrlPattern("/repository/{renderType}").matchMode(IMatchMode.STARTWITH).handler(NodeLet.class)
						.path("event").addUrlPattern("/event/").matchMode(IMatchMode.STARTWITH).handler(MonitorLet.class)
						.path("template").addUrlPattern("/template").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
						.path("doscript").addUrlPattern("/script").matchMode(EnumClass.IMatchMode.EQUALS).handler(ScriptDoLet.class)
						.path("upload").addUrlPattern("/upload").matchMode(IMatchMode.STARTWITH).handler(UploadLet.class).toBuilder() ;

		this.aradon = Aradon.create(cbuilder.build());
		this.radon = aradon.toRadon(9000);
		
		this.talkEngine = TalkEngine.create(aradon.getServiceContext()) ;
		radon.add("/websocket/{id}/{accessToken}", talkEngine) ;
		radon.add("/event/*", TalkMonitor.create(repoEntry.login())) ;
		
		status.set(Status.INITED);
		return this;
	}

	public ToonServer ready() throws Exception {
		this.talkEngine.init().startEngine() ;
		
		status.set(Status.READY);
		return this;
	}


	public ToonServer startRadon() throws Exception {
		if (status.get() != Status.READY) throw new IllegalStateException("current status is " + status.get()) ;
		
		radon.start().get();
		status.set(Status.STARTED);
		return this;
	}

	public ToonServer stop() throws InterruptedException, ExecutionException {
		talkEngine.stopEngine(); 
//		repoEntry.shutdown();
//		worker.shutdown(); 
//		worker.awaitTermination(2, TimeUnit.SECONDS) ;
		if (aradon != null) aradon.stop();
		if (radon != null) radon.stop().get();
		
		status.set(Status.STOPED);
		return this ;
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
