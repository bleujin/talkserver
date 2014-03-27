package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;

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

                wsession.pathBy("/ryun/test/1").property("messageId", "532ff616ca9ce6662c37a242").property("test", 1);
                wsession.pathBy("/ryun/test/2").property("messageId", "532ff616ca9ce6663137a242").property("test", 2);
                wsession.pathBy("/ryun/test/3").property("messageId", "532ff616ca9ce6662d37a242").property("test", 3);
                wsession.pathBy("/ryun/test/4").property("messageId", "532ff616ca9ce6662037a242").property("test", 4);
                wsession.pathBy("/ryun/test/5").property("messageId", "532ff616ca9ce6662e37a242").property("test", 5);

                //4 < 1 < 3 < 5 < 2

                return null;
            }
        });


        Debug.line(rsession.pathBy("/ryun/test").children().where("this.messageId >= '532ff616ca9ce6662c37a242'").toList());


    }


    public void testCompare() throws Exception {


    }

    @Override
    public void tearDown() throws Exception {
        entry.shutdown();
        super.tearDown();
    }
}
