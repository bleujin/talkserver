package net.ion.talkserver.config.builder;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.talk.ToonServer;
import net.ion.talkserver.config.TalkConfig;
import junit.framework.TestCase;

public class TestConfigBuilder extends TestCase{
	
	public void testCreate() throws Exception {
		TalkConfig config = ConfigBuilder.createDefault(9000).build() ;

		assertEquals("./resource/talk/", config.repoConfig().talkHomeDir()) ;
		assertEquals("61.250.201.157", config.serverConfig().hostName()) ;
	}

	public void testCreateWithConfig() throws Exception {
		TalkConfig config = ConfigBuilder.create("./resource/config/talk-config.xml").build() ;

		assertEquals("./resource/talk/", config.repoConfig().talkHomeDir()) ;
		assertEquals("61.250.201.157", config.serverConfig().hostName()) ;
	}
	

	
	public void testTestCreateServer() throws Exception {
		TalkConfig config = ConfigBuilder.createDefault(9000).build().addTestUser() ;
		ToonServer tserver = ToonServer.create(config);
		
		tserver.ready().startRadon() ;
		new InfinityThread().startNJoin();
		
		
	}

}
