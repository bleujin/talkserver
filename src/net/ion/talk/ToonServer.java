package net.ion.talk;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.craken.aradon.MonitorLet;
import net.ion.craken.aradon.NodeLet;
import net.ion.craken.aradon.UploadLet;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.authentication.BasicAuthenticationHandler;
import net.ion.nradon.handler.logging.LoggingHandler;
import net.ion.nradon.netty.NettyWebServer;
import net.ion.nradon.restlet.FileMetaType;
import net.ion.radon.core.let.PathHandler;
import net.ion.talk.filter.CrakenVerifier;
import net.ion.talk.filter.ToonVerifier;
import net.ion.talk.let.BotImageLet;
import net.ion.talk.let.LoginLet;
import net.ion.talk.let.PhoneAuthLet;
import net.ion.talk.let.ResourceLet;
import net.ion.talk.let.SVGLet;
import net.ion.talk.let.ScriptDoLet;
import net.ion.talk.let.ScriptExecLet;
import net.ion.talk.monitor.TalkMonitor;
import net.ion.talk.toonweb.ClientLet;
import net.ion.talk.toonweb.MobileClientLet;
import net.ion.talk.toonweb.ToonLogSink;
import net.ion.talk.toonweb.ToonWebResourceLet;

public class ToonServer {

	private enum Status {
		INITED, READY, STARTED, STOPED ;
	}
	
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
		FileMetaType.init(); 

		RadonConfiguration config = RadonConfiguration.newBuilder(9000)
			.add(new LoggingHandler(new ToonLogSink()))
			.add("/auth/*", new BasicAuthenticationHandler(CrakenVerifier.test(repoEntry.login())))
			.add("/auth/*", new PathHandler(LoginLet.class).prefixURI("/auth"))
			
			.add("/admin/*", new PathHandler(NodeLet.class, MonitorLet.class, ResourceLet.class, ScriptDoLet.class, UploadLet.class).prefixURI("admin"))
			
			.add("/bot/*", new PathHandler(BotImageLet.class))
			
			.add("/register/*", new PathHandler(PhoneAuthLet.class).prefixURI("/register"))
			.add("/execute/*", new PathHandler(ScriptExecLet.class))
			.add("/svg/*", new PathHandler(SVGLet.class))
			
			.add("/session/*", new BasicAuthenticationHandler(new ToonVerifier(repoEntry.login())))
			.add("/session/*", new PathHandler(ClientLet.class))
			
			.add("/mobile/*", new BasicAuthenticationHandler(new ToonVerifier(repoEntry.login())))
			.add("/mobile/*", new PathHandler(MobileClientLet.class))
			
			.add("/toonweb/*", new PathHandler(ToonWebResourceLet.class))
			.add("/upload/*", new PathHandler(net.ion.talk.let.UploadLet.class))
			.build() ;
		
		config.getServiceContext().putAttribute(RadonConfiguration.class.getCanonicalName(), config);
		config.getServiceContext().putAttribute("resourceDir", "./resource/toonweb") ;
		config.getServiceContext().putAttribute("templateDir", "./resource/template") ;
		config.getServiceContext().putAttribute(RepositoryEntry.EntryName, repoEntry);
		config.getServiceContext().putAttribute(ScheduledExecutorService.class.getCanonicalName(), worker);
		
		
        this.radon = new NettyWebServer(config);
        
		this.talkEngine = TalkEngine.create(radon.getConfig().getServiceContext()) ;
		radon.add("/websocket/{id}/{accessToken}", talkEngine) ;
		radon.add("/event/*", TalkMonitor.create(repoEntry.login())) ;
		
//		ConfigurationBuilder cbuilder = ConfigurationBuilder.newBuilder()	
//				.aradon()
//					.addAttribute(RepositoryEntry.EntryName, repoEntry)
//					.addAttribute(ScheduledExecutorService.class.getCanonicalName(), worker)
//				.sections()
//					.restSection("auth").addPreFilter(new ChallengeAuthenticator("users", verifier))
//						.path("login").addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
				
//					.restSection("register")
//						.path("smsAuth").addUrlPattern("/SMSAuth").matchMode(EnumClass.IMatchMode.STARTWITH).handler(PhoneAuthLet.class)
						
//					.restSection("script")
//						.path("script").addUrlPattern("/").matchMode(EnumClass.IMatchMode.EQUALS).handler(ScriptConfirmLet.class)
//						
//					.restSection("execute")
//						.path("execute").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
						
//					.restSection("resource")
//						.path("resource").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
						
//					.restSection("image")
//						.path("bot").addUrlPattern("/bot/icon/{botId}.jpg").handler(BotImageLet.class)
//						.path("bimage").addUrlPattern("/bimage/{botId}/").matchMode(IMatchMode.STARTWITH).handler(BotImageLet.class)
						
//					.restSection("svg")
//						.path("message").addUrlPattern("/message/{roomId}/{messageId}").handler(SVGLet.class)
//						.path("command").addUrlPattern("/command/{messageId}").handler(CommandSVGLet.class)
						
//					.restSection("session")
//						.addPreFilter(new ToonAuthenticator("user"))
//						.path("client").addUrlPattern("/").handler(ClientLet.class)
//						.path("reload").addUrlPattern("/reload").handler(ReloadLet.class)

//                    .restSection("mobile")
//                        .addPreFilter(new ToonAuthenticator("user"))
//                        .path("client").addUrlPattern("/").handler(MobileClientLet.class)

//					.restSection("toonweb")
//						.path("toonweb").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ToonWebResourceLet.class)
						
//					.restSection("upload")
//						.path("upload").addUrlPattern("/{userId}/{resource}").addUrlPattern("/{userId}/{resource}/{fieldname}").handler(net.ion.talk.let.UploadLet.class)
//						
//					.restSection("admin").addAttribute("baseDir", "./resource/template")
//						.path("node").addUrlPattern("/repository/{renderType}").matchMode(IMatchMode.STARTWITH).handler(NodeLet.class)
//						.path("event").addUrlPattern("/event/").matchMode(IMatchMode.STARTWITH).handler(MonitorLet.class)
//						.path("template").addUrlPattern("/template").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
//						.path("doscript").addUrlPattern("/script").matchMode(EnumClass.IMatchMode.EQUALS).handler(ScriptDoLet.class)
//						.path("upload").addUrlPattern("/upload").matchMode(IMatchMode.STARTWITH).handler(net.ion.craken.aradon.UploadLet.class).toBuilder() 
//					;

		
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
		if (radon != null) radon.stop().get();
		
		status.set(Status.STOPED);
		return this ;
	}


	private void checkStarted() {
		if (radon == null)
			throw new IllegalStateException("Aradon not started");
	}


	public ReadSession readSession() throws IOException {
		checkStarted();
		return repoEntry.login();
	}

	public TalkEngine talkEngine() {
		checkStarted();
		return radon.getConfig().getServiceContext().getAttributeObject(TalkEngine.class.getCanonicalName(), TalkEngine.class);
	}

	public ToonServer addAttribute(Object value) {
		radon.getConfig().getServiceContext().putAttribute(value.getClass().getCanonicalName(), value);
		return this;
	}

	public <T> T getAttribute(String key, Class<T> clz) {
		return radon.getConfig().getServiceContext().getAttributeObject(key, clz);
	}

	public String getHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}
}
