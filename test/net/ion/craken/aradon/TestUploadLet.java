package net.ion.craken.aradon;

import java.io.File;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.StringPart;
import net.ion.radon.aclient.multipart.FilePart;
import net.ion.radon.core.let.PathHandler;

public class TestUploadLet extends TestCase {

	private Radon radon;
	private NewClient nc;
	private RepositoryEntry rentry;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.radon = RadonConfiguration.newBuilder(9999).add(new PathHandler(UploadLet.class).prefixURI("/upload")).startRadon() ;
        this.rentry = radon.getConfig().getServiceContext().putAttribute("repository", RepositoryEntry.test()) ;
    	this.nc = NewClient.create() ;
    }
    
    @Override
    public void tearDown() throws Exception {
    	radon.stop().get() ;
    	nc.close() ;
    	super.tearDown();
    }

    public void testFirst() throws Exception {
    	Response response = nc.preparePost("http://localhost:9999/upload")
    			.addBodyPart(new StringPart("workspace", "test"))
    			.addBodyPart(new StringPart("path", "/node1/node2/node"))
    			.addBodyPart(new FilePart("uploadFile",  new File("./resource/template/js/jsoneditor-icons.png")))
    			.execute().get() ;

    	ReadSession session = rentry.login() ;
        assertTrue(session.exists("/node1/node2/node"));
        assertNotNull(session.pathBy("/node1/node2/node").property("blob"));
    }
    
    public void xtestNotMultipartPost() throws Exception {
    	Response response = nc.preparePost("http://localhost:9999/upload").execute().get() ;
    	
    	assertEquals(406, response.getStatus().getCode()); // not acceptable
    }
}
