package net.ion.craken.aradon;

import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.talk.let.ResourceLet;


public class AllTests extends TestCase{

	public static TestSuite suite(){
		TestSuite suite = new TestSuite() ;
		
		suite.addTestSuite(TestNodeLet.class);
		suite.addTestSuite(TestStaticFileLet.class);
		suite.addTestSuite(TestUploadLet.class);
		
		return suite ;
	}
	
	public void xtestDeploy() throws Exception {
		
		final RepositoryImpl r = RepositoryImpl.inmemoryCreateWithTest() ;
		ConfigurationBuilder cbuilder = ConfigurationBuilder.newBuilder().aradon()
		.addAttribute("repository", r)
		.sections()
            .restSection("admin").addAttribute("baseDir", "./resource/template")
				.path("node").addUrlPattern("/repository/{workspace}/{renderType}").matchMode(IMatchMode.STARTWITH).handler(NodeLet.class)
                .path("template").addUrlPattern("/template").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ResourceLet.class)
                .path("upload").addUrlPattern("/upload").matchMode(IMatchMode.STARTWITH).handler(UploadLet.class).toBuilder() ;

		final Radon radon = Aradon.create(cbuilder.build()).toRadon(9000).start().get() ;
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				try {
					r.shutdown() ;
					radon.stop().get() ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		new InfinityThread().startNJoin(); 
	}
}
