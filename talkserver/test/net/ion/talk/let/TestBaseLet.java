package net.ion.talk.let;

import net.ion.talk.ToonServer;
import junit.framework.TestCase;

public class TestBaseLet extends TestCase{

	protected ToonServer tserver;
    @Override
	protected void setUp() throws Exception {
		super.setUp();
		this.tserver = ToonServer.testWithLoginLet();
	}

    @Override
    public void tearDown() throws Exception {
        tserver.stop() ;
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

}
