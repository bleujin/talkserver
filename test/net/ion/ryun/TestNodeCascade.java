package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.*;
import net.ion.framework.util.Debug;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 14.
 * Time: 오후 5:57
 * To change this template use File | Settings | File Templates.
 */
public class TestNodeCascade extends TestCase{

    private RepositoryEntry rentry;
    private ReadSession rsession;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rentry = RepositoryEntry.test();
        rsession = rentry.login();

    }

    public void testFirst() throws Exception {

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/notify/").child("1234");
                wsession.pathBy("/notify/").child("5678");
                wsession.pathBy("/notify/").refTos("unread", "/notify/1234");
                wsession.pathBy("/notify/").refTos("unread", "/notify/5678");
                return null;
            }
        });


        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/notify/1234").removeSelf();
                return null;
            }
        });

        IteratorList<ReadNode> unreadNodes = rsession.pathBy("/notify").refs("unread");
        while(unreadNodes.hasNext()){
            final ReadNode node = unreadNodes.next();
            if(node.isGhost()){
                Debug.line("read");
                rsession.tran(new TransactionJob<Object>() {
                    @Override
                    public Object handle(WriteSession wsession) throws Exception {
                        wsession.pathBy("/notify").unRefTos("unread", node.fqn().toString());
                        return null;
                    }
                });
                Debug.line(rsession.pathBy("notify").refChildren("unread").toList());
            }else{
                Debug.line(node);
            }
        }

    }

    public void testTwo() throws Exception {

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/notify/").child("1234");
                wsession.pathBy("/notify/").child("5678");
                wsession.pathBy("/notify/unread").child("1234");
                wsession.pathBy("/notify/unread").child("5678");
                return null;
            }
        });


        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/notify/1234").removeSelf();
                return null;
            }
        });

        IteratorList<ReadNode> unreadNodes = rsession.pathBy("/notify").refs("unread");
        while(unreadNodes.hasNext()){
            ReadNode node = unreadNodes.next();
            Debug.line(node);
            Debug.line(node.isGhost());
        }

    }

    @Override
    public void tearDown() throws Exception {
        rentry.shutdown();
        super.tearDown();
    }
}
