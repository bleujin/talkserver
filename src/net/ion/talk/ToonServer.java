package net.ion.talk;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.craken.aradon.CrakenLet;
import net.ion.craken.aradon.MiscLet;
import net.ion.craken.aradon.MonitorLet;
import net.ion.craken.aradon.NodeLet;
import net.ion.craken.aradon.UploadLet;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.authentication.BasicAuthenticationHandler;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.nradon.handler.logging.LoggingHandler;
import net.ion.nradon.netty.NettyWebServer;
import net.ion.nradon.restlet.FileMetaType;
import net.ion.radon.core.let.PathHandler;
import net.ion.radon.handler.AppCacheHandler;
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
import net.ion.talkserver.config.TalkConfig;
import net.ion.talkserver.config.builder.ConfigBuilder;

public class ToonServer {

	private enum Status {
		STOPED, INITED, READY, STARTED ;
	}
	
	private Radon radon;
	private AtomicReference<Status> status = new AtomicReference<ToonServer.Status>(Status.STOPED) ;
	
	private final RepositoryEntry repoEntry;
	private final ScheduledExecutorService worker;
	private final TalkConfig tconfig;
	
	private TalkEngine talkEngine;

	private ToonServer(RepositoryEntry rentry, TalkConfig tconfig){
		this.repoEntry = rentry ;
		this.worker = tconfig.executorService() ;
		this.tconfig = tconfig ;
	}

	public static ToonServer create(TalkConfig tconfig) throws Exception{
		return new ToonServer(tconfig.createREntry(), tconfig).init() ;
	}
	
	public static ToonServer testCreate() throws Exception {
		TalkConfig tconfig = new ConfigBuilder().build();
		return new ToonServer(tconfig.testREntry(), tconfig).init() ;
	}


	private ToonServer init() throws Exception {
		if (status.get().ordinal() >= Status.INITED.ordinal()) return this ;
		final ReadSession session = repoEntry.login() ;
		RadonConfiguration config = RadonConfiguration.newBuilder(tconfig.serverConfig().port())
			.add(new HttpHandler(){
				@Override
				public void onEvent(EventType eventtype, Radon radon) {
				}

				@Override
				public int order() {
					return -11;
				}

				@Override
				public void handleHttpRequest(HttpRequest httprequest, HttpResponse httpresponse, HttpControl httpcontrol) throws Exception {
					if (! session.workspace().cache().getStatus().allowInvocations()) {
						httpresponse.status(200).content("ALREADY STOPPING STATE").end() ;
					} else {
						httpcontrol.nextHandler(); 
					}
				}
			})
			.add(new LoggingHandler(new ToonLogSink()))
			.add("/auth/*", new BasicAuthenticationHandler(CrakenVerifier.test(repoEntry.login())))
			.add("/auth/*", new PathHandler(LoginLet.class).prefixURI("/auth"))
			
			.add("/admin/*", new PathHandler(NodeLet.class, CrakenLet.class, MonitorLet.class, ResourceLet.class, ScriptDoLet.class, UploadLet.class, MiscLet.class).prefixURI("admin"))
			
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
			.add(new HttpHandler(){
				@Override
				public void onEvent(EventType eventtype, Radon radon) {
				}
	
				@Override
				public int order() {
					return 1000;
				}
	
				@Override
				public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
					response.status(404).content("not found path : " + request.uri()).end() ;
				}
			}).build() ;
		
		config.getServiceContext().putAttribute(RadonConfiguration.class.getCanonicalName(), config);
		config.getServiceContext().putAttribute("resourceDir", tconfig.repoConfig().webHomeDir()) ;
		config.getServiceContext().putAttribute("templateDir", tconfig.repoConfig().templateHomeDir()) ;
		config.getServiceContext().putAttribute(RepositoryEntry.EntryName, repoEntry);
		config.getServiceContext().putAttribute(ScheduledExecutorService.class.getCanonicalName(), worker);
		
		
        this.radon = new NettyWebServer(config);
        
		this.talkEngine = TalkEngine.create(radon.getConfig().getServiceContext()) ;
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
		if (status.get().ordinal() < Status.READY.ordinal()) {
			ready();
		}
		
		radon.start().get();
		status.set(Status.STARTED);
		return this;
	}
	
	

	public ToonServer stop() throws InterruptedException, ExecutionException {
		if (status.get() == Status.STOPED) return this ;
		
		status.set(Status.STOPED);

		
		talkEngine.stopEngine(); 
		if (radon != null) radon.stop().get();
		worker.shutdown(); 
		worker.awaitTermination(1, TimeUnit.SECONDS) ;

		System.err.println("ToonServer Stopped : " + new Date());
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
		return tconfig.serverConfig().hostName() ;
	}

	public TalkConfig config() {
		return tconfig;
	}
}
