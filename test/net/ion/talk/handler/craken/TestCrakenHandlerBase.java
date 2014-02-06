package net.ion.talk.handler.craken;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 6:30
 * To change this template use File | Settings | File Templates.
 */
public class TestCrakenHandlerBase extends TestCase{

    protected ReadSession rsession;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        RepositoryEntry repoEntry = RepositoryEntry.test();
        rsession = repoEntry.login();
    }

    @Override
    public void tearDown() throws Exception {

        super.tearDown();
    }
}
