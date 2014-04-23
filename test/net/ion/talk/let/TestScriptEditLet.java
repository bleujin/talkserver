package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.client.AradonClient;
import net.ion.radon.core.EnumClass;
import net.ion.talk.TalkScript;

import org.restlet.Response;
import org.restlet.data.Method;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Author: Ryunhee Han Date: 2013. 12. 26.
 */
public class TestScriptEditLet extends TestBaseLet {

	@Override
	public void setUp() throws Exception {
		super.setUp();

		tserver.cbuilder().aradon().sections().restSection("script").path("script").addUrlPattern("/").matchMode(EnumClass.IMatchMode.STARTWITH).handler(ScriptEditLet.class).build();

		// tserver.startAradon() ;
		tserver.startRadon();
		TalkScript ts = TalkScript.create(tserver.readSession(), Executors.newScheduledThreadPool(1));
		ts.readDir(new File("./script"), true) ;
		tserver.addAttribute(ts) ;

	}

	public void testMergeScript() {
		AradonClient ac = tserver.mockClient().fake();
		Response response = ac.createRequest("/script/").handle(Method.GET);
		assertEquals(200, response.getStatus().getCode());
		Debug.line(response.getEntityAsText());
	}
	
	public void xtestDeploy() throws Exception {
		new InfinityThread().startNJoin(); 
	}

}
