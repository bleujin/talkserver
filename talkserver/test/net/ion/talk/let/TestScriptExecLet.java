package net.ion.talk.let;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.EnumClass.IMatchMode;
import org.restlet.data.Method;

import java.net.InetAddress;

public class TestScriptExecLet extends TestBaseLet{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tserver.cbuilder().aradon()
		.sections().restSection("aradon")
		.path("jscript").addUrlPattern("/jscript/{name}.{format}").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
		.build();
		tserver.startRadon() ;

	}
		
	public void testHttpScript() throws Exception {
		NewClient client = tserver.mockClient().real() ;
		String script = "session.tranSync(function(wsession){" +
				"	wsession.pathBy('/bleujin').property('name', params.asString('name')).property('age', params.asInt('age'));" +
				"}) ;" +
				"" +
				"session.pathBy('/bleujin').toRows('name, age').toString();" ;
		Request request = new RequestBuilder()
			.setMethod(Method.POST)
			.setUrl("http://" + InetAddress.getLocalHost().getHostAddress()+":9000/aradon/jscript/bleujin.string")
				.addParameter("name", "bleujin").addParameter("age", "20")
				.addParameter("script", script).build();

		Response response = client.executeRequest(request).get();
		
		Debug.line(response.getTextBody()) ;
		client.close() ;
	}
	
	public void testImage() throws Exception {
		NewClient client = tserver.mockClient().real() ;
		String script = "session.tranSync(function(wsession){" +
		"	wsession.pathBy('/bleujin').property('name', params.asString('name')).property('age', params.asInt('age')).property('image', params.asStrema('image')) ;" +
		"}) ;" +
		"" +
		"session.pathBy('/bleujin').blob('image').toInputStream();" ;
		
		
	}
	
}
