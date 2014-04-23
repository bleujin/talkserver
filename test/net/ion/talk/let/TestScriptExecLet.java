package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.*;
import net.ion.radon.client.AradonClient;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.talk.TalkScript;

import org.restlet.data.Method;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class TestScriptExecLet extends TestBaseLet {

	private ReadSession session;
	private TalkScript ts;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tserver.cbuilder().aradon().sections().restSection("execute").path("execute").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class).build();
		tserver.startRadon();
		session = tserver.readSession();
		this.ts = TalkScript.create(session, Executors.newScheduledThreadPool(1));
		ts.readDir(new File("./script")) ;
		tserver.addAttribute(ts);
	}

	public void testAjaxHello() throws Exception {
		NewClient client = tserver.mockClient().real();

		Response response = client.executeRequest(new RequestBuilder().setMethod(Method.POST).setUrl("http://" + tserver.getHostAddress() + ":9000/execute/test/hello.json").build()).get();
		assertEquals(200, response.getStatusCode());
		assertEquals("application/json; charset=UTF-8", response.getContentType());
		JsonObject obj = JsonObject.fromString(response.getTextBody()).asJsonObject("result");
		assertEquals("ryun", obj.asString("name"));
		Debug.line(obj);
		

		Response responseAsString = client.executeRequest(new RequestBuilder().setMethod(Method.POST).setUrl("http://" + tserver.getHostAddress() + ":9000/execute/test/hello.string").build()).get();
		assertEquals("text/plain; charset=UTF-8", responseAsString.getContentType());
		obj = JsonObject.fromString(responseAsString.getTextBody()).asJsonObject("result");
		assertEquals("ryun", obj.asString("name"));
		client.close();
	}
	
	public void testOnException() throws Exception {
		NewClient client = tserver.mockClient().real();

		Response response = client.executeRequest(new RequestBuilder().setMethod(Method.POST).setUrl("http://" + tserver.getHostAddress() + ":9000/execute/test/onexception.json").build()).get();
		assertEquals(200, response.getStatusCode());
		assertEquals("application/json; charset=UTF-8", response.getContentType());
		JsonObject resJson = JsonObject.fromString(response.getTextBody());
		assertEquals("failure", resJson.asString("status"));
	}
	

	public void testAjaxGreetingWithParams() throws Exception {
		NewClient client = tserver.mockClient().real();
		RequestBuilder requestBuilder = new RequestBuilder().setMethod(Method.POST)
					.addParameter("name", "alex").addParameter("location", "oregon").addParameter("money", "10000").addParameter("friends", "joshua");

		Request request = requestBuilder.setUrl("http://" + tserver.getHostAddress() + ":9000/execute/test/greeting.json").build();
		Response response = client.executeRequest(request).get();

		assertEquals(200, response.getStatusCode());

		assertEquals("application/json; charset=UTF-8", response.getContentType());
		JsonObject obj = JsonObject.fromString(response.getTextBody()).asJsonObject("result");
		assertEquals("alex", obj.get("name").getAsString());
		assertEquals("oregon", obj.get("location").getAsString());
		assertEquals(10000.0, obj.get("money").getAsDouble());
		assertEquals("joshua", obj.get("friends").getAsJsonObject().get("name").getAsString());

	}

}
