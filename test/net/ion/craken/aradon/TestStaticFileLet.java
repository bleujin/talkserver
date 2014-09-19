package net.ion.craken.aradon;

import javax.ws.rs.core.HttpHeaders;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.client.StubServer;
import net.ion.radon.core.let.PathHandler;
import net.ion.talk.let.ResourceLet;

public class TestStaticFileLet extends TestCase {

	private StubServer ss;
    @Override
    public void setUp() throws Exception {
    	this.ss = StubServer.create(ResourceLet.class) ;
    	ss.treeContext().putAttribute("baseDir", "./resource/template") ;
    	
    }

    public void testFile() throws Exception {
    	StubHttpResponse response = ss.request("/template/js/jsoneditor-icons.png").get() ;
    	String hvalue = response.header(HttpHeaders.CONTENT_TYPE) ;
    	
        Debug.line(response.contents().length) ;
        assertEquals("image/png", hvalue);
    }

    public void testNotExistFile() throws Exception {
        String notExistFile = "/resource/chart2.png";
		StubHttpResponse response = ss.request(notExistFile).get() ;
        assertEquals(404, response.status());
    }

    
    public void testRadon() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(8800).add(new PathHandler(ResourceLet.class)).start().get() ;
		radon.getConfig().getServiceContext().putAttribute("baseDir", "./resource/template") ;
		
		NewClient nc = NewClient.create() ;
		Response response = nc.prepareGet("http://localhost:8800/template/js/jsoneditor-icons.png").execute().get() ;
		
		Debug.line(response.getContentType()) ;
		
		
		radon.stop().get() ;
		
	}
    
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

}
