package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.framework.util.Debug;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.util.AradonTester;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestMessageSVGLet extends TestCase {

	private RepositoryEntry r;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.r = RepositoryEntry.test() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		this.r.shutdown() ;
		super.tearDown();
	}
	
	
	public void testGet() throws Exception {
		
		Aradon aradon = AradonTester.create().register("svg", "/message/{roomId}/{type}/{messageId}.svg", OldSVGLet.class).getAradon() ;
		aradon.getServiceContext().putAttribute("repository", this.r) ;
		
		AradonClient ac = AradonClientFactory.create(aradon) ;
		Response response = ac.createRequest("/svg/message/roomroom/sender/12345.svg?charId=bat").handle(Method.GET) ;
	
		Debug.line(response.getEntityAsText()) ;
	}
}
