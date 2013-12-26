package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.InfinityThread;
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
                .path("/toontalk").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ScriptLet.class).build();


        ReadSession session = rentry.login("test");
        session.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.createBy("script/test");
                wsession.createBy("script/test/child1");
                wsession.createBy("script/test/child2");
                return null;
            }
        });

        aradon = Aradon.create(config);
        aradon.startServer(9000);
        Radon radon = aradon.toRadon(9000);
        radon.start();
        new InfinityThread().startNJoin();
    }

    public void testGetBasicPage() throws Exception {
        Response response = ac.createRequest("/script/toontalk/test").handle(Method.GET);
        assertEquals(200, response.getStatus().getCode());
    }

    public void testGetNode(){
        Response response = ac.createRequest("/script/toontalk/testScript").handle(Method.GET);
        assertEquals(200, response.getStatus().getCode());
    }


    @Override
    public void tearDown() throws Exception {
        aradon.stop();
        super.tearDown();
    }
}

