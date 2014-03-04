package net.ion.craken.aradon;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;

public class TestCrakenBase extends TestCase {

    protected RepositoryImpl r;
    protected ReadSession session;
    
    protected final String WS_NAME = "test";

    @Override
    public void setUp() throws Exception {
        this.r = RepositoryImpl.inmemoryCreateWithTest(); // pre define "test" ;

        r.start();
        this.session = r.login("test");
    }


    @Override
    public void tearDown() throws Exception {
    	r.shutdown();
        super.tearDown();
    }

}
