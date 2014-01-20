package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.client.AradonClient;
import net.ion.radon.core.EnumClass;
import org.restlet.Response;
import org.restlet.data.Method;

/**
 * Author: Ryunhee Han
 * Date: 2013. 12. 26.
 */
public class TestScriptLet extends TestBaseLet{


    @Override
    public void setUp() throws Exception {
        super.setUp();

        tserver.cbuilder().aradon()
                    .sections()
                	.restSection("script")
                	.path("script").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ScriptLet.class).build();
		
		//tserver.startAradon() ;
//        tserver.startRadon();

    }

    public void testMergeScript(){
    	AradonClient ac = tserver.mockClient().fake() ; 
        Response response = ac.createRequest("/script/test/post").addParameter("script","HelloTest").handle(Method.POST);
        assertEquals(301, response.getStatus().getCode());
        response = ac.createRequest("/script/test/post").handle(Method.GET);
        assertTrue(response.getEntityAsText().contains("HelloTest"));
    }

    public void testViewScript() throws Exception {
		ReadSession session = tserver.readSession() ;
        session.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("script/test").property("script", "hello");
                wsession.pathBy("script/test/child1").property("script", "ryun");
                wsession.pathBy("script/test/child2").property("script", "bleujin");
                return null;
            }
        });

    	Response response = tserver.mockClient().fake().createRequest("/script/test").handle(Method.GET);
        assertEquals(200, response.getStatus().getCode());
        assertTrue(response.getEntityAsText().contains("script"));
        assertTrue(response.getEntityAsText().contains("hello"));
        assertTrue(response.getEntityAsText().contains("child1"));
        assertTrue(response.getEntityAsText().contains("child2"));

        new InfinityThread().startNJoin();
    }


}

