package net.ion.talk.let;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.util.AradonTester;

/**
 * Author: Ryunhee Han Date: 2013. 12. 27.
 */
public class TestResourceLet extends TestCase {

	private Radon radon;
	private NewClient nc;

	@Override
	public void setUp() throws Exception {
		this.radon = AradonTester.create().register("resource", "/{path}", "resource", IMatchMode.STARTWITH, ResourceLet.class).getAradon().toRadon(9000) ;
		radon.start().get() ;
		this.nc = NewClient.create() ;
	}
	
	@Override
	public void tearDown() throws Exception {
		radon.stop() ;
		nc.close(); 
		super.tearDown();
	}

	public void testFileExist() throws Exception {
		Response response = nc.prepareGet("http://" + InetAddress.getLocalHost().getHostAddress() + ":9000/resource/jquery-1.10.2.min.js").execute().get();
		FileInputStream fis = new FileInputStream("./resource/jquery-1.10.2.min.js");
		assertEquals(fis.getChannel().size(), response.getBodyAsBytes().length);
		fis.close(); 
	}

	public void testFileNotExist() throws IOException, ExecutionException, InterruptedException {
		Response response = nc.prepareGet("http://" + InetAddress.getLocalHost().getHostAddress() + ":9000/resource/notFound").execute().get();
		assertEquals(404, response.getStatusCode());
	}

	public void testToonWeb() throws Exception {
		Response response = nc.prepareGet("http://" + InetAddress.getLocalHost().getHostAddress() + ":9000/resource/toonweb/swfobject.js").execute().get();
		assertEquals(200, response.getStatusCode());
	}

}
