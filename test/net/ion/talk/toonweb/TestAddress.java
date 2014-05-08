package net.ion.talk.toonweb;

import org.restlet.resource.Get;

import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.util.AradonTester;
import net.ion.talk.util.NetworkUtil;
import junit.framework.TestCase;

public class TestAddress extends TestCase {

	
	public void testAtAddress() throws Exception {
		Aradon aradon = AradonTester.create().register("bot", "/{botId}/{pwd}", EchoLet.class).getAradon() ;
		Radon radon = aradon.toRadon(9000).start().get() ;
		
		NewClient nc = NewClient.create() ;

		Response response = nc.prepareGet(NetworkUtil.httpAddress(9000, "/bot/bleujin@i-on.net/123456")).execute().get() ;
		
		Debug.line(response.getTextBody()) ;

		nc.close(); 
		radon.stop().get() ;
	}
}


class EchoLet implements IServiceLet {
	
	@Get
	public String echo(@AnRequest InnerRequest request, @PathParam("botId") String botId, @PathParam("pwd") String pwd){
		return botId + "/" + pwd;
	}
}