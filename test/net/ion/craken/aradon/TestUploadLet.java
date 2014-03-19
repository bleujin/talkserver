package net.ion.craken.aradon;

import java.io.File;
import java.io.IOException;

import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.HttpMultipartEntity;
import net.ion.radon.core.EnumClass;
import net.ion.radon.util.AradonTester;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestUploadLet extends TestCrakenBase {

    private AradonTester tester;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tester = AradonTester.create().putAttribute("repository", r).register("", "/upload", EnumClass.IMatchMode.EQUALS, UploadLet.class);
    }

    public void testFirst() throws IOException {
        Request request = new Request(Method.POST, "riap://component/upload");

        String nodePath = "/node1/node2/node";
        File uploadFile = new File("./resource/template/js/jsoneditor-icons.png");

        HttpMultipartEntity entity = new HttpMultipartEntity();

        entity.addParameter("workspace", "test");
        entity.addParameter("path", nodePath);
        entity.addParameter("uploadFile", uploadFile);

        request.setEntity(entity.makeRepresentation());
        tester.getAradon().handle(request);

        assertTrue(session.exists(nodePath));
        assertNotNull(session.pathBy(nodePath).property("blob"));
    }
    
    public void testNotMultipartPost() throws IOException {
    	AradonClient client = AradonClientFactory.create(tester.getAradon());
    	Response response = client.createRequest("/upload").handle(Method.POST);
    	
    	assertEquals(406, response.getStatus().getCode()); // not acceptable
    }
}
