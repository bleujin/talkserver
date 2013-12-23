package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;

import org.restlet.Response;
import org.restlet.data.Method;

public class TestAuth extends TestCase {

	private ReadSession session;
	private Aradon aradon;
	private CrakenVerifier verifier;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final RepositoryEntry rentry = RepositoryEntry.test();
		this.session = rentry.login("test") ;

		verifier = CrakenVerifier.test(session);
		
		Configuration config = ConfigurationBuilder.newBuilder().aradon()
		.addAttribute(RepositoryEntry.EntryName, rentry)
		.sections().restSection("auth")
			.addPreFilter(new ChallengeAuthenticator("users", verifier))
				.path("login")
				.addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class)
		.build();

		
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/servers/bleujin").property("host", "61.250.201.157").property("port", 9000) ;
				wsession.pathBy("/users/emanon").property("id", "admin").property("pwd", "emanon") ;
				return null;
			}
			
		}) ;
		
		aradon = Aradon.create(config);
		aradon.start() ;
	}
	
	public void testBasicAuth() throws Exception {
		
		AradonClient ac = AradonClientFactory.create(aradon);
		IAradonRequest request = ac.createRequest("/auth/login", "bleujin", "1234");
		Response r = request.handle(Method.GET);
		assertEquals(401, r.getStatus().getCode());
		
		verifier.addUsr("bleujin", "1234") ;
		assertEquals(200, request.handle(Method.GET).getStatus().getCode());
	}
	
	public void testGetWebsocketURL() throws Exception {
		AradonClient ac = AradonClientFactory.create(aradon);
		IAradonRequest request = ac.createRequest("/auth/login", "emanon", "emanon");
		
		Response r = request.handle(Method.GET);
		assertEquals(true, r.getEntityAsText().startsWith("ws://61.250.201.157:9000/websocket/emanon/")) ;
	}
	
	
}
