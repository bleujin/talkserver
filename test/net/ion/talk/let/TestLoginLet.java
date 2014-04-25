package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.filter.CrakenVerifier;
import net.ion.talk.util.NetworkUtil;

import org.restlet.data.Method;

public class TestLoginLet extends TestCase {

	private Radon radon;
	private RepositoryEntry repoEntry;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.repoEntry = RepositoryEntry.test();
		CrakenVerifier verifier = CrakenVerifier.test(repoEntry.login());
		
		Configuration configuration = ConfigurationBuilder.newBuilder()	
			.aradon()
				.addAttribute(RepositoryEntry.EntryName, repoEntry)
			.sections()
				.restSection("auth").addPreFilter(new ChallengeAuthenticator("users", verifier))
					.path("login").addUrlPattern("/login").matchMode(IMatchMode.STARTWITH).handler(LoginLet.class).build() ;
		
		ReadSession rsession = repoEntry.login();
		rsession.tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/servers/" + wsession.workspace().repository()).property("host", NetworkUtil.hostAddress()).property("port", 9000);
				wsession.pathBy("/users/ryun").property("pushId", "C6833");
				return null;
			}
		});
		
		radon = Aradon.create(configuration).toRadon() ;
		radon.start().get() ;
	}

	@Override
	public void tearDown() throws Exception {
		repoEntry.shutdown(); 
		radon.stop().get() ;
		super.tearDown();
	}
	
	public void testInvalidAuth() throws Exception {
		NewClient nc = NewClient.create() ;
		
		Realm invalidRealm = new RealmBuilder().setPrincipal("ryun").setPassword("invalid push Id").build() ;
		Request request = new RequestBuilder().setUrl(NetworkUtil.httpAddress(9000, "/auth/login")).setMethod(Method.POST).setRealm(invalidRealm).build() ;
		net.ion.radon.aclient.Response response = nc.prepareRequest(request).execute().get() ;
		assertEquals(401, response.getStatus().getCode());
		
		nc.close(); 
	}

	public void testBasicAuth() throws Exception {
		NewClient nc = NewClient.create() ;
		
		Realm invalidRealm = new RealmBuilder().setPrincipal("ryun").setPassword("C6833").build() ;
		Request request = new RequestBuilder().setUrl(NetworkUtil.httpAddress(9000, "/auth/login")).setMethod(Method.POST).setRealm(invalidRealm).build() ;
		net.ion.radon.aclient.Response response = nc.prepareRequest(request).execute().get() ;
		assertEquals(200, response.getStatus().getCode());

		Debug.line(response.getTextBody());
		assertEquals(true, response.getTextBody().startsWith(NetworkUtil.wsAddress(9000, "/websocket/ryun/")));
		nc.close(); 
	}


}
