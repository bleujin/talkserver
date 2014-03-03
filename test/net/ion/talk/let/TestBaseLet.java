package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.talk.ToonServer;

public class TestBaseLet extends TestCase {

	protected ToonServer tserver;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.tserver = ToonServer.testWithLoginLet();
	}

	@Override
	public void tearDown() throws Exception {
		tserver.stop();
		super.tearDown();
	}

}
