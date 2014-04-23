package net.ion.talk;

import net.ion.craken.aradon.NodeLet;
import net.ion.craken.aradon.UploadLet;
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
import net.ion.talk.filter.CrakenVerifier;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.handler.TalkHandlerGroup;
import net.ion.talk.let.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ToonServer {



    public static ToonServer testWithLoginLet() throws Exception {
		return new ToonServer().init();
	}

    private RhinoEntry rengine;
    private RepositoryEntry rentry;
	private CrakenVerifier verifier;
	private Aradon aradon;
	private Radon radon;
	private ConfigurationBuilder cbuilder;
    private TalkHandlerGroup talkHandlerGroup;
	private MockClient mockClient;

	private ToonServer init() throws Exception {
		this.rentry = RepositoryEntry.test();
		this.verifier = CrakenVerifier.test(rentry.login());
        this.rengine = RhinoEntry.test();
		this.talkHandlerGroup = TalkHandlerGroup.create();

		this.cbuilder = ConfigurationBuilder.newBuilder().aradon()
		.addAttribute(RepositoryEntry.EntryName, rentry)
		.addAttribute(RhinoEntry.EntryName, rengine)
		.sections()
            .restSection("auth")
			    .addPreFilter(new ChallengeAuthenticator("users", verifier))
                .path("login")
                .addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
            .restSection("register")
                .path("/SMSAuth")
                .addUrlPattern("/SMSAuth").matchMode(IMatchMode.STARTWITH).handler(SMSAuthLet.class)
            .restSection("script")
                .path("script").addUrlPattern("/").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ScriptEditLet.class)
            .restSection("execute")
                .path("execute").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
            .restSection("resource")
                .path("resource").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH)
                .handler(ResourceLet.class)
            .restSection("bot")
                .path("bot").addUrlPattern("").matchMode(IMatchMode.STARTWITH).handler(EmbedBotLet.class)
                
            .restSection("admin").addAttribute("baseDir", "./resource/template").addAttribute("repository", rentry.repository())
				.path("node").addUrlPattern("/repository/{workspace}/{renderType}").matchMode(IMatchMode.STARTWITH).handler(NodeLet.class)
                .path("template").addUrlPattern("/template").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
                .path("upload").addUrlPattern("/upload").matchMode(IMatchMode.STARTWITH).handler(UploadLet.class)                
			.restSection("websocket")
				.addAttribute(TalkHandlerGroup.class.getCanonicalName(), talkHandlerGroup)
				.wspath("websocket")
				.addUrlPattern("/{id}/{accessToken}").handler(TalkEngine.class).toBuilder() ;

		this.mockClient = MockClient.create(this) ;
		return this;
	}

    public static long GMTTime(){
    	return GregorianCalendar.getInstance().getTime().getTime() ;
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
        rentry.shutdown();
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

	public ReadSession readSession() throws IOException {
		checkStarted();
		return rentry.login();
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

    public ToonServer addAttribute(Object value){
        aradon.getServiceContext().putAttribute(value.getClass().getCanonicalName(), value) ;
        return this ;
    }

    public <T> T getAttribute(String key, Class<T> clz){
    	 return aradon.getServiceContext().getAttributeObject(key, clz) ;
    }

    public String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
