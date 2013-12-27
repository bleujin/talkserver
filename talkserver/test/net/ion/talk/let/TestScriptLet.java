package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.Radon;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.config.Configuration;
import net.ion.talk.ToonServer;

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
		
		tserver.startAradon() ;
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
                wsession.createBy("script/test").property("script", "hello");
                wsession.createBy("script/test/child1").property("script", "ryun");
                wsession.createBy("script/test/child2").property("script", "bleujin");
                return null;
            }
        });

    	Response response = tserver.mockClient().fake().createRequest("/script/test").handle(Method.GET);
        assertEquals(200, response.getStatus().getCode());
        assertTrue(response.getEntityAsText().contains("hello"));
        assertTrue(response.getEntityAsText().contains("child1"));
        assertTrue(response.getEntityAsText().contains("child2"));
    }

}

