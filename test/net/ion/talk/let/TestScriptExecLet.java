package net.ion.talk.let;

import java.io.File;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.let.PathHandler;
import net.ion.talk.script.TalkScript;
import net.ion.talk.util.NetworkUtil;

import org.jboss.netty.handler.codec.http.HttpMethod;

public class TestScriptExecLet extends TestCase {
	
	private Radon radon;
	private NewClient client;
	private RepositoryImpl repo;

	@Override
	public void setUp() throws Exception {

		radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(ScriptExecLet.class).prefixURI("/execute")).startRadon() ;
		
		this.repo = RepositoryImpl.inmemoryCreateWithTest() ;
		ReadSession rsession = repo.login("test");
		
		TalkScript ts = TalkScript.create(rsession, Executors.newScheduledThreadPool(3));
		ts.readDir(new File("./script"), true) ;
		radon.getConfig().getServiceContext().putAttribute(TalkScript.class.getCanonicalName(), ts) ;
		this.client = NewClient.create() ;
	}
	
	@Override
	public void tearDown() throws Exception {
		radon.stop() ;
		repo.shutdown() ;
		client.close(); 
		super.tearDown();
	}

	public void testAjaxHello() throws Exception {
		Response response = client.executeRequest(new RequestBuilder().setMethod(HttpMethod.POST).setUrl(NetworkUtil.httpAddress(9000, "/execute/test/hello.json")).build()).get();
		assertEquals(200, response.getStatusCode());
		assertEquals("application/json; charset=UTF-8", response.getContentType());
		JsonObject obj = JsonObject.fromString(response.getTextBody()).asJsonObject("result");
		assertEquals("ryun", obj.asString("name"));
		Debug.line(obj);
		

		Response responseAsString = client.executeRequest(new RequestBuilder().setMethod(HttpMethod.POST).setUrl(NetworkUtil.httpAddress(9000, "/execute/test/hello.string")).build()).get();
		assertEquals("text/plain; charset=UTF-8", responseAsString.getContentType());
		obj = JsonObject.fromString(responseAsString.getTextBody()).asJsonObject("result");
		assertEquals("ryun", obj.asString("name"));
		client.close();
	}
	
	public void testOnException() throws Exception {
		Response response = client.executeRequest(new RequestBuilder().setMethod(HttpMethod.POST).setUrl(NetworkUtil.httpAddress(9000, "/execute/test/onexception.json")).build()).get();
		assertEquals(200, response.getStatusCode());
		assertEquals("application/json; charset=UTF-8", response.getContentType());
		Debug.line(response.getTextBody());
		JsonObject resJson = JsonObject.fromString(response.getTextBody());
		assertEquals("failure", resJson.asString("status"));
	}
	

	public void testAjaxGreetingWithParams() throws Exception {
		
		
		RequestBuilder requestBuilder = new RequestBuilder().setMethod(HttpMethod.POST)
					.addParameter("name", "alex").addParameter("location", "oregon").addParameter("money", "10000").addParameter("friends", "joshua");

		Request request = requestBuilder.setUrl(NetworkUtil.httpAddress(9000, "/execute/test/greeting.json")).build();
		Response response = client.executeRequest(request).get();

		assertEquals(200, response.getStatusCode());

		assertEquals("application/json; charset=UTF-8", response.getContentType());
		JsonObject obj = JsonObject.fromString(response.getTextBody()).asJsonObject("result");
		assertEquals("alex", obj.get("name").getAsString());
		assertEquals("oregon", obj.get("location").getAsString());
		assertEquals(10000.0, obj.get("money").getAsDouble());
		assertEquals("joshua", obj.get("friends").getAsJsonObject().get("name").getAsString());
		Debug.line(response.getTextBody()) ;
		
	}

}
