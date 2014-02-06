package net.ion.talk;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.Radon;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.let.*;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ToonServer {

    public static ToonServer testWithLoginLet() throws Exception {
		return new ToonServer().init();
	}

	private ReadSession session;
    private RhinoEntry rengine;
	private CrakenVerifier verifier;
	private Aradon aradon;
	private Radon radon;
	private ConfigurationBuilder cbuilder;
	private TalkHandlerGroup talkHandlerGroup;
	private MockClient mockClient;
    private Map<String, Object> propertyMap = MapUtil.newMap();

	private ToonServer init() throws Exception {
		rengine = RhinoEntry.test() ;
		final RepositoryEntry rentry = RepositoryEntry.test();
		this.session = rentry.login() ;
		this.verifier = CrakenVerifier.test(session);

		this.talkHandlerGroup = TalkHandlerGroup.create();
		talkHandlerGroup.addHandler(ServerHandler.test()) ;

		this.cbuilder = ConfigurationBuilder.newBuilder().aradon()
		.addAttribute(RepositoryEntry.EntryName, rentry)
		.addAttribute(RhinoEntry.EntryName, rengine)
		.sections()
            .restSection("auth")
			    .addPreFilter(new ChallengeAuthenticator("users", verifier))
				.path("login")
				.addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
            .restSection("script")
                .path("script").addUrlPattern("/").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ScriptEditLet.class)
            .restSection("execute")
                .path("execute").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
            .restSection("static")
                .path("static").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH)
                .handler(StaticFileLet.class)
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
			throw new IllegalStateException("Aradon not started") ;
	}

	@Deprecated
	public CrakenVerifier verifier() {
		return verifier;
	}

	public ReadSession readSession() {
		checkStarted();
		return session;
	}

	public TalkEngine talkEngine() {
		checkStarted() ;
		return aradon.getServiceContext().getAttributeObject(TalkEngine.class.getCanonicalName(), TalkEngine.class) ;
	}


	public MockClient mockClient() {
		return mockClient;
	}


    public RhinoEntry rhinoEntry() {
        return rengine;
    }

    public void addAttribute(String key, Object value){
        propertyMap.put(key, value);
    }

    public <T> T getAttribute(String key, Class<T> clz){
        return (T) propertyMap.get(key);
    }

    public String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }

    }
}
