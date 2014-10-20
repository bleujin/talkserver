package net.ion.talk.deploy;

import junit.framework.TestCase;
import net.ion.framework.util.InfinityThread;
import net.ion.talk.ToonServer;
import net.ion.talkserver.config.TalkConfig;
import net.ion.talkserver.config.builder.ConfigBuilder;


public class TestToonServerNew extends TestCase {

	public void testRunInfinite() throws Exception {
		TalkConfig tconfig = ConfigBuilder.createDefault(9000).build().addTestUser() ;
//		FileUtil.deleteDirectory(new File(tconfig.repoConfig().talkHomeDir()) ;

		final ToonServer tserver = ToonServer.create(tconfig);
		tserver.ready().startRadon();
		
        Runtime.getRuntime().addShutdownHook(new Thread(){
        	public void run(){
        		try {
					tserver.stop();
				} catch (Exception e) {
					e.printStackTrace();
				} 
        	}
        });
        
        new InfinityThread().startNJoin();
    }
}
