package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.*;
import net.ion.radon.client.AradonClient;
import net.ion.radon.core.EnumClass.IMatchMode;
import org.restlet.data.Method;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

public class TestScriptExecLet extends TestBaseLet {

	private ReadSession session;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tserver.cbuilder().aradon().sections().restSection("execute").path("execute").addUrlPattern("/").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class).build();
		tserver.startRadon();
		session = tserver.readSession();
	}

	public void testAjaxScript() throws Exception {
		NewClient client = tserver.mockClient().real();
		String script = "rb.create().newInner().property('name','ryun').build().toJsonObject();";
		RequestBuilder requestBuilder = new RequestBuilder().setMethod(Method.POST).addParameter("script", script);

		Request request = requestBuilder.setUrl("http://" + tserver.getHostAddress() + ":9000/execute/ajax.json" + "").build();

		Response response = client.executeRequest(request).get();
		assertEquals(200, response.getStatusCode());
		assertEquals("application/json; charset=UTF-8", response.getContentType());
		JsonObject obj = JsonObject.fromString(response.getTextBody()).asJsonObject("result");
		assertEquals("ryun", obj.asString("name"));

		request = requestBuilder.setUrl("http://" + tserver.getHostAddress() + ":9000/execute/ajax.string").build();

		response = client.executeRequest(request).get();
		assertEquals("text/plain; charset=UTF-8", response.getContentType());
		obj = JsonObject.fromString(response.getTextBody()).asJsonObject("result");
		assertEquals("ryun", obj.asString("name"));
		client.close();
	}

	public void testAjaxScriptWithParams() throws Exception {
		NewClient client = tserver.mockClient().real();
		String script = "rb.create().newInner()" + ".property('name', params.asString('name'))" + ".property('location', params.asString('location'))" + ".property('money', params.asInt('money'))" + ".inner('friends').property('name', params.asString('friends')).build().toJsonObject();";

		RequestBuilder requestBuilder = new RequestBuilder().setMethod(Method.POST).addParameter("script", script).addParameter("name", "alex").addParameter("location", "oregon").addParameter("money", "10000").addParameter("friends", "joshua");

		Request request = requestBuilder.setUrl("http://" + tserver.getHostAddress() + ":9000/execute/ajax.json").build();
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
