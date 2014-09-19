package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.authentication.BasicAuthenticationHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.core.let.PathHandler;
import net.ion.talk.filter.CrakenVerifier;
import net.ion.talk.util.NetworkUtil;

import org.jboss.netty.handler.codec.http.HttpMethod;

public class TestLoginLet extends TestCase {

	private Radon radon;
	private RepositoryEntry repoEntry;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.repoEntry = RepositoryEntry.test();
		
		radon = RadonConfiguration.newBuilder(9000)
				.add("/auth/*", new BasicAuthenticationHandler(CrakenVerifier.test(repoEntry.login())))
				.add("/auth/*", new PathHandler(LoginLet.class).prefixURI("/auth")).startRadon() ;
		radon.getConfig().getServiceContext().putAttribute(RepositoryEntry.EntryName, repoEntry) ;
		
		ReadSession rsession = repoEntry.login();
		rsession.tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/servers/" + wsession.workspace().repository()).property("host", NetworkUtil.hostAddress()).property("port", 9000);
				wsession.pathBy("/users/ryun").property("pushId", "C6833");
				return null;
			}
		});

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
		Request request = new RequestBuilder().setUrl(NetworkUtil.httpAddress(9000, "/auth/login")).setMethod(HttpMethod.POST).setRealm(invalidRealm).build() ;
		net.ion.radon.aclient.Response response = nc.prepareRequest(request).execute().get() ;
		assertEquals(401, response.getStatus().getCode());
		
		nc.close(); 
	}

	public void testBasicAuth() throws Exception {
		NewClient nc = NewClient.create() ;
		
		Realm invalidRealm = new RealmBuilder().setPrincipal("ryun").setPassword("C6833").build() ;
		Request request = new RequestBuilder().setUrl(NetworkUtil.httpAddress(9000, "/auth/login")).setMethod(HttpMethod.POST).setRealm(invalidRealm).build() ;
		net.ion.radon.aclient.Response response = nc.prepareRequest(request).execute().get() ;
		assertEquals(200, response.getStatus().getCode());

		Debug.line(response.getTextBody());
		assertEquals(true, response.getTextBody().startsWith(NetworkUtil.wsAddress(9000, "/websocket/ryun/")));
		nc.close(); 
	}


}
