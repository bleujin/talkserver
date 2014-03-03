package net.ion.talk.let;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;

import org.antlr.stringtemplate.StringTemplate;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

public class ClientLet implements IServiceLet {

	@Get @Post
	public Representation viewPage(
				@AnContext TreeContext context, @AnRequest InnerRequest request, @PathParam("roomId") String roomId) throws IOException, Exception {

		String wsPath = new LoginLet().login(context, request).getText() ;
		
		String userId = request.getClientInfo().getUser().getIdentifier() ;
		RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class) ;
		
		ReadSession session = rentry.login() ;
		
		// net.ion.toon.aradon.ClientLet
		String fileName = context.getAttributeObject(ClientLet.class.getCanonicalName(), "./resource/toonweb/chat.tpl", String.class);
		File tplFile = new File(fileName);
		if (!tplFile.exists())
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "not found template file : " + fileName);

		
		final StringWriter template = new StringWriter();
		IOUtil.copyNClose( new FileReader(tplFile), template) ;
		
		StringTemplate st = new StringTemplate(template.toString());
		Map<String, String> configMap = MapUtil.newMap();
		configMap.put("address", wsPath);
		configMap.put("sender", roomId);
		configMap.put("topicId", roomId);
		st.setAttribute("config", configMap);

		return new StringRepresentation(st.toString(), MediaType.TEXT_HTML, Language.ALL);
	}
}
