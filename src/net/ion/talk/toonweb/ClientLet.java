package net.ion.talk.toonweb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.handler.authentication.BasicAuthenticationHandler;
import net.ion.radon.core.ContextParam;
import net.ion.talk.bean.Const.User;
import net.ion.talk.let.LoginLet;

import org.antlr.stringtemplate.StringTemplate;
import org.jboss.resteasy.spi.HttpRequest;

@Path("/session")
public class ClientLet {

	@GET @POST
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public Response viewPage(@ContextParam("repository") RepositoryEntry rentry, @Context HttpRequest request) throws IOException, InterruptedException, ExecutionException {

		final String userId = (String) request.getAttribute(BasicAuthenticationHandler.USERNAME);
		ReadSession session = rentry.login();
		String websocketURI = LoginLet.targetAddress(session, userId);

		// net.ion.toon.aradon.ClientLet
		String fileName = "./resource/toonweb/chat.htm";
		File tplFile = new File(fileName);
		if (!tplFile.exists())
			return Response.status(Status.NOT_FOUND).build() ;

		final StringWriter template = new StringWriter();
		IOUtil.copyNClose(new FileReader(tplFile), template);

		String nickName = session.pathBy("/users/" + userId).property(User.NickName).asString();
		StringTemplate st = new StringTemplate(template.toString());
		Map<String, String> configMap = MapUtil.<String> chainKeyMap().put("address", websocketURI).put("sender", userId).put("nickName", nickName).toMap();

		st.setAttribute("config", configMap);

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/connections/" + userId).property("source", "web");
				return null;
			}
		});

		return Response.ok().entity(st.toString()).build() ;
	}

	@GET
	@POST
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
	@Path("/reload")
	public String websocketURI(@ContextParam("repository") RepositoryEntry rentry, @Context HttpRequest request) throws IOException, InterruptedException, ExecutionException {
		final String userId = (String) request.getAttribute(BasicAuthenticationHandler.USERNAME);
		ReadSession session = rentry.login();
		String websocketURI = LoginLet.targetAddress(session, userId);

		return websocketURI;
	}
}
