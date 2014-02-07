package net.ion.talk.let;

import java.io.IOException;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.Fqn;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.AnResponse;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.MultiValueMap;

import net.ion.talk.ToonServer;
import org.restlet.Response;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Author: Ryunhee Han Date: 2013. 12. 26.
 */
public class ScriptEditLet implements IServiceLet {

	@Get
	public StringRepresentation viewScript(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException {
		RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		ReadSession session = rentry.login();
		String requestPath = "/script" + request.getPathReference().getPath();
		ReadNode node = session.ghostBy(requestPath);
		
		String result = node.transformer(new ScriptTemplate(session.workspace().parseEngine())) ;
		return new StringRepresentation(result, MediaType.TEXT_HTML, Language.valueOf("UTF-8"));
	}

	
	@Delete
	public String deleteScript(@AnContext TreeContext context, @AnRequest InnerRequest request, @AnResponse Response response) throws Exception{
        RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		final String requestPath = "/script" + request.getPathReference().getPath();
		ReadSession session = rentry.login();

		Fqn parent = session.tranSync(new TransactionJob<Fqn>() {
			@Override
			public Fqn handle(WriteSession wsession) throws Exception {
				WriteNode found = wsession.pathBy(requestPath);
				found.removeSelf() ;
				return found.fqn().getParent();
			}
		});

		response.redirectPermanent(parent.toString());
		return "";
	}
	
	@Post
	public String mergeScript(@AnContext TreeContext context, @AnRequest InnerRequest request, @AnResponse Response response, final @FormParam("script") String script) throws Exception {
		RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		final String requestPath = "/script" + request.getPathReference().getPath();
		ReadSession session = rentry.login();

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy(requestPath).property("script", script);
				return null;
			}
		});

		response.redirectPermanent(requestPath);
		return "";
	}
}
