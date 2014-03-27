package net.ion.ryun.extend;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.*;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.framework.util.Debug;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 27.
 * Time: 오전 11:22
 * To change this template use File | Settings | File Templates.
 */
public class TestReadNode extends TestCase{

    public void testReadNode() throws Exception {


        RepositoryEntry rentry = RepositoryEntry.test();
        ReadSession rsession = rentry.login();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/ryun/test/1").property("num", "1");
                wsession.pathBy("/ryun/test/2").property("num", "2");
                wsession.pathBy("/ryun/test/3").property("num", "3");
                wsession.pathBy("/ryun/test/4").property("num", "4");
                wsession.pathBy("/ryun/test/5").property("num", "5");
                return null;
            }
        });

        assertTrue(rsession.pathBy("ryun/test/1") instanceof NodeCommon);
        assertTrue(rsession.pathBy("ryun/test").children() instanceof AbstractChildren);

    }
}
