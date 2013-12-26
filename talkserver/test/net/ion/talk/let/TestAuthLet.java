package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
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

public class TestAuthLet extends TestCase {

	private ReadSession session;
	private Aradon aradon;
	private CrakenVerifier verifier;
    private AradonClient ac;


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
				wsession.pathBy("/users/emanon").property("id", "emanon").property("pwd", "emanon") ;
				return null;
			}
		}) ;
		
		aradon = Aradon.create(config);
		aradon.start() ;
        ac = AradonClientFactory.create(aradon);

	}
	
	public void testBasicAuth() throws Exception {
		IAradonRequest request = ac.createRequest("/auth/login", "bleujin", "1234");
		assertEquals(401, request.handle(Method.GET).getStatus().getCode());
		verifier.addUser("bleujin", "1234") ;
		assertEquals(200, request.handle(Method.GET).getStatus().getCode());
	}

	public void testGetWebsocketURL() throws Exception {
		IAradonRequest request = ac.createRequest("/auth/login", "emanon", "emanon");
		Response r = request.handle(Method.GET);
		assertEquals(true, r.getEntityAsText().startsWith("ws://61.250.201.157:9000/websocket/emanon/")) ;
	}

    @Override
    public void tearDown() throws Exception {
        aradon.stop();
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
