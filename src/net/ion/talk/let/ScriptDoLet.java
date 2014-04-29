package net.ion.talk.let;

import java.io.IOException;
import java.sql.SQLException;

import javax.script.ScriptException;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.db.Rows;
import net.ion.framework.rest.HTMLFormater;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.script.TalkScript;

import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class ScriptDoLet implements IServiceLet {

	@Get
	public Representation viewScript(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException {
		TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);
		ReadSession session = ts.session() ;
		ReadNode node = session.root() ;
		
		String result = node.transformer(new ScriptTemplate(session.workspace().parseEngine(), ts.fullFnNames(), "doscript.tpl")) ;
		return new StringRepresentation(result, MediaType.TEXT_HTML, Language.valueOf("UTF-8"));
	}

	@Post
	public Representation runScript(@AnContext TreeContext context, @AnRequest InnerRequest request, @FormParam("script") String script) throws IOException {
		TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);
		ReadSession session = ts.session() ;

		try {
			Rows rows = ts.viewRows(session, script);
			return rows.toHandle(new HTMLFormater()) ;
		} catch (ScriptException e) {
			return new StringRepresentation(e.getMessage()) ;
		} catch (SQLException e) {
			return new StringRepresentation(e.getMessage()) ;
		}
	}

}
