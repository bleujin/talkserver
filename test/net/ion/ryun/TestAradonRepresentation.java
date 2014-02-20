package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.radon.util.AradonTester;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 1:25
 * To change this template use File | Settings | File Templates.
 */
public class TestAradonRepresentation extends TestCase implements IServiceLet{

    public void testResponse() throws Exception {

        Aradon aradon = AradonTester.create().register("test", "/{string}",  TestAradonRepresentation.class).getAradon();
        aradon.startServer(9000);

        AradonClient ac = AradonClientFactory.create(aradon);
        Response response = ac.createRequest("/test/one").handle(Method.GET);

        assertEquals(200, response.getStatus().getCode());
        assertEquals("application/json", response.getEntity().getMediaType().toString());


        net.ion.radon.aclient.Response ncResponse = NewClient.create().prepareGet("http://localhost:9000/test/two").execute().get();
        assertEquals(200, ncResponse.getStatusCode());
        assertEquals("application/json; charset=UTF-8", ncResponse.getContentType());


    }

    @Get
    public Representation get(@AnRequest InnerRequest request){


        Debug.line(request.getAttribute("string"));

        JsonObject jsonObject = JsonObject.fromString("{\"name\":\"ryun\"}");

        return new JsonObjectRepresentation(jsonObject);
    }


}
