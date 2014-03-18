package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 18.
 * Time: 오후 4:53
 * To change this template use File | Settings | File Templates.
 */
public class TestQuery extends TestCase{

    private RepositoryEntry entry;
    private ReadSession rsession;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        entry = RepositoryEntry.test();
        rsession = entry.login();
    }


    public void testFirst() throws Exception {

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/users/6929").property("name", "ryun");


                return null;
            }
        });

        Debug.line(rsession.pathBy("/users/").childQuery("name:6929").find().size());

    }

    @Override
    public void tearDown() throws Exception {
        entry.shutdown();
        super.tearDown();
    }
}
