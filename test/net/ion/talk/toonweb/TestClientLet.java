package net.ion.talk.toonweb;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;
import net.ion.craken.aradon.NodeLet;
import net.ion.craken.aradon.UploadLet;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.talk.TalkEngine;
import net.ion.talk.bean.Const.User;
import net.ion.talk.filter.ToonAuthenticator;
import net.ion.talk.let.PhoneAuthLet;
import net.ion.talk.let.ResourceLet;
import net.ion.talk.let.ScriptDoLet;
import net.ion.talk.let.UserLet;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import org.restlet.data.Method;

public class TestClientLet extends TestCase {

	private Radon radon;
	private RepositoryEntry rentry;
	private TalkEngine talkEngine;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.rentry = RepositoryEntry.test();
		ReadSession session = rentry.login();

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
//				wsession.pathBy("/servers/home").property("host", "61.250.201.157").property("port", 9000);
				wsession.pathBy("/users/bleujin").property("password", "1234");
				return null;
			}
		});
		ConfigurationBuilder cbuilder = ConfigurationBuilder.newBuilder()
				.aradon()
					.addAttribute(RepositoryEntry.EntryName, rentry)
					.addAttribute(ScheduledExecutorService.class.getCanonicalName(), Executors.newScheduledThreadPool(3))
				.sections()
					.restSection("admin").addAttribute("baseDir", "./resource/template")
						.path("node").addUrlPattern("/repository/{renderType}").matchMode(IMatchMode.STARTWITH).handler(NodeLet.class)
						.path("template").addUrlPattern("/template").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
						.path("doscript").addUrlPattern("/script").matchMode(EnumClass.IMatchMode.EQUALS).handler(ScriptDoLet.class)
						.path("upload").addUrlPattern("/upload").matchMode(IMatchMode.STARTWITH).handler(UploadLet.class)
					.restSection("session")
						.addPreFilter(new ToonAuthenticator("user"))
						.path("client").addUrlPattern("/").handler(ClientLet.class)
						.path("reload").addUrlPattern("/reload").handler(ReloadLet.class)
					.restSection("register")
						.path("user").addUrlPattern("/user/{email}").matchMode(IMatchMode.STARTWITH).handler(UserLet.class)
						.path("phoneAuth").addUrlPattern("/phoneAuth").matchMode(EnumClass.IMatchMode.STARTWITH).handler(PhoneAuthLet.class)
					.restSection("toonweb")
						.path("toonweb").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ToonWebResourceLet.class).toBuilder();
						

		Aradon aradon = Aradon.create(cbuilder.build()) ;
	
		
		this.radon = aradon.toRadon(9000)
					.start().get();
		
		this.talkEngine = TalkEngine.testCreate(aradon.getServiceContext()) ;
		
		radon.add("/websocket/{id}/{accessToken}", talkEngine) ;
	}

	@Override
	protected void tearDown() throws Exception {
		this.talkEngine.stopEngine(); 
		this.rentry.shutdown();
		this.radon.stop();
		super.tearDown();
	}

	public void testPost() throws Exception {
		talkEngine.startEngine() ;
		NewClient nc = NewClient.create();
		Realm realm = new RealmBuilder().setPrincipal("bleujin").setPassword("1234").build();
		Request request = new RequestBuilder(Method.POST).setRealm(realm).setUrl("http://localhost:9000/session/bleujin@i-on.net/roomroom").build();
		Response response = nc.prepareRequest(request).execute().get();

		Debug.line(response.getTextBody());
		nc.close();
	}
	
	public void testRun() throws Exception {
		talkEngine.init().startEngine() ;
		ReadSession rsession = talkEngine.readSession();
		
		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/hero@i-on.net").property(User.UserId, "hero@i-on.net").property(User.Password, "1").property(User.NickName, "hero").property(User.StateMessage, "-_-;").property(User.Phone, "1042216492") ;
				wsession.pathBy("/users/bleujin@i-on.net").property(User.UserId, "bleujin@i-on.net").property(User.Password, "1").property(User.NickName, "bleujin").property(User.StateMessage, "-_-a").property(User.Phone, "1042216492") ;
				wsession.pathBy("/users/airkjh@i-on.net").property(User.UserId, "airkjh@i-on.net").property(User.Password, "1").property(User.NickName, "airkjh").property(User.StateMessage, "-_-a").property(User.Phone, "1091399660") ;
				
				wsession.pathBy("/rooms/roomroom/members/airkjh@i-on.net") ;
				return null;
			}
		});
		
		new InfinityThread().startNJoin(); 
	}
	
	
	
	
	public void xtestResponseBuilder() throws Exception {
		ReadSession rsession = talkEngine.readSession();
		rsession.tran(new TransactionJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/bleujin").property("options", "{event:'Hello'}") ;
				return null;
			}
		}) ;
		
		Debug.line(rsession.pathBy("/bleujin").property("options").asString()) ;
		
		JsonObject json = TalkResponseBuilder.create().newInner().property(rsession.pathBy("/bleujin"), "options").build().toJsonObject();
		Debug.line(json) ;
			
		JsonObject fromJson = JsonObject.fromString(json.toString()) ;
		Debug.line(fromJson.asJsonObject("options")) ;
		
	}
	
}
