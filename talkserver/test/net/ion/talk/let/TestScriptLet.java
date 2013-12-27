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
import org.restlet.Response;
import org.restlet.data.Method;

/**
 * Author: Ryunhee Han
 * Date: 2013. 12. 26.
 */
public class TestScriptLet extends TestCase{


    private Aradon aradon;
    private AradonClient ac;

    @Override
    public void setUp() throws Exception {
        super.setUp();


        final RepositoryEntry rentry = RepositoryEntry.test();
        Configuration config = Configuration.newBuilder().aradon()
                .addAttribute(RepositoryEntry.EntryName, rentry)
                .sections().restSection("script")
                .path("").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ScriptLet.class).build();


        ReadSession session = rentry.login("test");
        session.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.createBy("script/test").property("script", "hello");
                wsession.createBy("script/test/child1").property("script", "ryun");
                wsession.createBy("script/test/child2").property("script", "bleujin");
                return null;
            }
        });

        aradon = Aradon.create(config);
//        aradon.startServer(9000);
        ac = AradonClientFactory.create(aradon);
        Radon radon = aradon.toRadon(9000);
        radon.start();
        new InfinityThread().startNJoin();
    }

    public void testGetPage() throws Exception {
        Response response = ac.createRequest("/script/test").handle(Method.GET);
        assertEquals(200, response.getStatus().getCode());
        assertTrue(response.getEntityAsText().contains("hello"));
        assertTrue(response.getEntityAsText().contains("child1"));
        assertTrue(response.getEntityAsText().contains("child2"));
    }

    public void testPostPage(){
        Response response = ac.createRequest("/script/test/post").addParameter("script","HelloTest").handle(Method.POST);
        assertEquals(301, response.getStatus().getCode());
        response = ac.createRequest("/script/test/post").handle(Method.GET);
        assertTrue(response.getEntityAsText().contains("HelloTest"));
    }

    @Override
    public void tearDown() throws Exception {
        aradon.stop();
        super.tearDown();
    }
}

