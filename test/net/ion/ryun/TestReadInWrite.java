package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 15.
 * Time: 오후 2:27
 * To change this template use File | Settings | File Templates.
 */
public class TestReadInWrite extends TestCase{


    private RepositoryEntry rentry;
    private ReadSession rsession;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        rentry = RepositoryEntry.test();
        rsession = rentry.login();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/ryun").property("name", "Ryunhee Han");
                return null;
            }
        });

    }

    public void testFirst() throws Exception {

        final ReadNode ryun = rsession.pathBy("/users/ryun");

        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                Debug.line(ryun.property("name").stringValue());
                return null;
            }
        });

    }
}
