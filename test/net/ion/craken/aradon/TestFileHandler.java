package net.ion.craken.aradon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

@Path("/resource")
public class TestFileHandler extends TestCase {

	@GET
	@Path("/file/{remain : .*}")
	public File viewFile(@PathParam("remain") String remainPath){
		return new File("./resource", remainPath) ;
	}
	
	@GET
	@Path("/sout/{remain : .*}")
	public StreamingOutput streamFile(@PathParam("remain") final String remainPath){
		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				FileInputStream fis = new FileInputStream(new File("./resource", remainPath)); 
				IOUtil.copy(fis, output);
			}
		};
	}
	
	public void testViewFile() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		StubHttpResponse response = ss.request("/resource/file/log4j.properties").get() ;
		
		Debug.line(response.header(HttpHeaders.CONTENT_TYPE)) ;
	}
}
