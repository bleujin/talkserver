package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.util.AradonTester;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 28.
 */
public class TestParameterLet extends TestCase implements IServiceLet{


    public void testFormArrayParams() throws Exception {

        Aradon aradon = AradonTester.create().register("test", "/", EnumClass.IMatchMode.STARTWITH, TestParameterLet.class).getAradon();
        aradon.toRadon(9000).start().get();


//        Representation entity = AradonClientFactory.create(aradon).createRequest("/test/").addHeader("Content-Type", "application/x-www-form-urlencoded").setEntity(new StringRepresentation("phone=1&phone=2")).handle(Method.POST).getEntity();//.addParameter("phone", "1").addParameter("phone", "2").handle(Method.POST).getEntity();


        Response response = NewClient.create().preparePost("http://localhost:9000/test/").addParameter("phone", "1").addParameter("phone", "2").execute().get();

        new InfinityThread().startNJoin();


    }


    @Post
    public void post(@AnRequest InnerRequest request){
        request.getEntity();
    }
}
