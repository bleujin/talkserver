package net.ion.talk.let;

import java.io.IOException;
import java.sql.SQLException;

import javax.script.ScriptException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.db.Rows;
import net.ion.radon.core.TreeContext;
import net.ion.talk.misc.ScriptTemplate;
import net.ion.talk.script.HTMLFormater;
import net.ion.talk.script.TalkScript;

@Path("/script")
public class ScriptDoLet {

	private TalkScript ts;

	public ScriptDoLet(@Context TreeContext context) {
		this.ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String viewScript(@Context TreeContext context) throws IOException {
		TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);
		ReadSession session = ts.session() ;
		ReadNode node = session.root() ;
		
		String result = node.transformer(new ScriptTemplate(session.workspace().parseEngine(), ts.fullFnNames(), "doscript.tpl")) ;
		return result ; 
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String runScript(@FormParam("script") String script) throws IOException {
		ReadSession session = ts.session() ;

		try {
			Rows rows = ts.viewRows(session, script);
			return rows.toHandle(new HTMLFormater()).toString() ;
		} catch (ScriptException e) {
			return e.getMessage() ;
		} catch (SQLException e) {
			return e.getMessage() ;
		}
	}

}
