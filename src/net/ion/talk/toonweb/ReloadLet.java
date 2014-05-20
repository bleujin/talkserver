package net.ion.talk.toonweb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.antlr.stringtemplate.StringTemplate;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.bean.Const.User;
import net.ion.talk.let.LoginLet;

public class ReloadLet implements IServiceLet{
	

	@Get @Post
	public Representation viewPage(
				@ContextParam("repository") RepositoryEntry rentry, @AnRequest InnerRequest request) throws IOException, InterruptedException, ExecutionException{
		
		final String userId = request.getClientInfo().getUser().getIdentifier() ;
		ReadSession session = rentry.login() ;
		String websocketURI = LoginLet.targetAddress(session, userId) ;
		
		return new StringRepresentation(websocketURI, MediaType.TEXT_HTML, Language.ALL);
	}
}
