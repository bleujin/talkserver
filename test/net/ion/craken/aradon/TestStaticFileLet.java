package net.ion.craken.aradon;

import junit.framework.TestCase;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.EnumClass;
import net.ion.radon.util.AradonTester;
import net.ion.talk.let.ResourceLet;

import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

public class TestStaticFileLet extends TestCase {

    private AradonClient ac;

    @Override
    public void setUp() throws Exception {
    	AradonTester tester = AradonTester.create().putAttribute("baseDir", "./resource").register("resource", "/", EnumClass.IMatchMode.STARTWITH, ResourceLet.class);
        ac = AradonClientFactory.create(tester.getAradon());
    	
    }

    public void testFile() {
        Representation rep = ac.createRequest("/resource/template/js/jsoneditor-icons.png").get();
        assertEquals(true, rep instanceof InputRepresentation);
        assertEquals("image/png", rep.getMediaType().toString());
    }

    public void testNotExistFile() {
        String notExistFile = "/resource/chart2.png";
		Response response = ac.createRequest(notExistFile).handle(Method.GET);
		
        assertEquals(404, response.getStatus().getCode());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

}
