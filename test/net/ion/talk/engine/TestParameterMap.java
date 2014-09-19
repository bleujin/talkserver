package net.ion.talk.engine;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.multipart.FilePart;
import net.ion.radon.aclient.multipart.StringPart;
import net.ion.radon.core.let.PathHandler;
import net.ion.talk.ParameterMap;
import net.ion.talk.util.NetworkUtil;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.plugins.providers.multipart.FormDataHandler;
import org.jboss.resteasy.plugins.providers.multipart.InputBody;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.spi.HttpRequest;

public class TestParameterMap extends TestCase {

	private Radon radon;
	private NewClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.radon = RadonConfiguration.newBuilder(8999).add(new PathHandler(ParamLet.class, ParamsLet.class).prefixURI("/test")).startRadon() ;
		this.client = NewClient.create();
	}

	@Override
	protected void tearDown() throws Exception {
		radon.stop().get();
		client.close();
		super.tearDown();
	}

	public void testFormParamType() throws Exception {

		Response response = client.preparePost(NetworkUtil.httpAddress(8999, "/test/param")).addParameter("string", "안녕").addParameter("int", "1").addParameter("long", "1").execute().get();
		assertEquals("ok", response.getTextBody());
	}

	public void testMultiPartForm() throws Exception {

		Response response = client.preparePut(NetworkUtil.httpAddress(8999, "/test/param"))
				.addBodyPart(new StringPart("string", "안녕"))
				.addBodyPart(new StringPart("int", "1")).addBodyPart(new StringPart("long", "1"))
				.addBodyPart(new FilePart("file", new File("./resource/testScript.js"))).execute()
				.get();

		assertEquals("ok", response.getTextBody());
	}

	public void xtestParamsType() throws Exception {

		final Request request = new RequestBuilder().setUrl(NetworkUtil.httpAddress(8999, "/test/params")).setMethod(HttpMethod.POST)
					.addParameter("string", "안녕").addParameter("string", "안녕2").build();
		Debug.line(request.getParams());

		Response response = client.executeRequest(request).get();
		Debug.line("ok", response.getTextBody());
	}

}

@Path("/param")
class ParamLet {

	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String viewStremaParam(MultipartFormDataInput input) throws IOException {
		final ParameterMap params = ParameterMap.create() ;
		input.dataHandle(new FormDataHandler<Void>() {
			@Override
			public Void handle(InputBody ibody) throws IOException {
				params.addValue(ibody.name(), ibody.asString()) ;
				return null;
			}
		}) ;

		Debug.line(params.asString("string"), params.asStrings("string"), params.asInt("int"), params.asString("file"));
		return "ok";
	}

	@POST
	public String viewParam(@Context HttpRequest req, @FormParam("string") String svalue) {
		ParameterMap params = ParameterMap.create(req);
		Debug.line(req.getFormParameters().getFirst("string"), params.asString("string"), params.asInt("int"), params.asLong("long"), svalue);

		return "ok";
	}
}

@Path("/params")
class ParamsLet  {
	@POST
	public String viewParams(@Context HttpRequest req) {

		ParameterMap params = ParameterMap.create(req);

		Debug.line(params.asStrings("string"));
		return "ok";
	}

}
