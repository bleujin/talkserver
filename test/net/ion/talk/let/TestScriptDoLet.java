package net.ion.talk.let;

import java.io.File;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.let.PathHandler;
import net.ion.talk.script.TalkScript;
import net.ion.talk.util.NetworkUtil;

public class TestScriptDoLet extends TestCase {
	
	private Radon radon;
	private NewClient nc;
	private RepositoryImpl repo;

	@Override
	public void setUp() throws Exception {
		radon = RadonConfiguration.newBuilder(9000)
			.add(new PathHandler(ScriptDoLet.class))
			.add(new PathHandler(ResourceLet.class)).startRadon() ;
		
		this.repo = RepositoryImpl.inmemoryCreateWithTest() ;
		ReadSession rsession = repo.login("test");
		TalkScript ts = TalkScript.create(rsession, Executors.newScheduledThreadPool(3));
		ts.readDir(new File("./script"), true) ;
		
		radon.getConfig().getServiceContext().putAttribute(TalkScript.class.getCanonicalName(), ts) ;
		this.nc = NewClient.create() ;
	}
	
	@Override
	public void tearDown() throws Exception {
		radon.stop() ;
		repo.shutdown() ;
		nc.close(); 
		super.tearDown();
	}
	
	public void testViewPage() throws Exception {
		net.ion.radon.aclient.Response response = nc.prepareGet(NetworkUtil.httpAddress(9000, "/script")).execute().get() ;
		assertEquals(200, response.getStatus().getCode());
		Debug.line(response.getStatusText(), response.getTextBody());
	}
	
		
	public void xtestDeploy() throws Exception {
		new InfinityThread().startNJoin(); 
	}

}
