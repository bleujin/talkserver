package net.ion.talk.toonweb;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.handler.authentication.BasicAuthenticationHandler;
import net.ion.radon.core.ContextParam;
import net.ion.talk.let.LoginLet;

import org.jboss.resteasy.spi.HttpRequest;


@Path("/session")
public class ReloadLet {
	

	@GET @POST
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
	@Path("/reload")
	public String viewPage(
				@ContextParam("repository") RepositoryEntry rentry, @Context HttpRequest request) throws IOException, InterruptedException, ExecutionException{
		final String userId = (String) request.getAttribute(BasicAuthenticationHandler.USERNAME) ;
		ReadSession session = rentry.login() ;
		String websocketURI = LoginLet.targetAddress(session, userId) ;
		
		return websocketURI;
	}
}
