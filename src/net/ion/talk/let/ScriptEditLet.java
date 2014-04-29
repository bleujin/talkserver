package net.ion.talk.let;

import java.io.IOException;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.script.TalkScript;

import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

/**
 * Author: Ryunhee Han Date: 2013. 12. 26.
 */
public class ScriptEditLet implements IServiceLet {

	@Get
	public StringRepresentation viewScript(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException {
		TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);
		ReadSession session = ts.session() ;
		ReadNode node = session.root() ;
		
		String result = node.transformer(new ScriptTemplate(session.workspace().parseEngine(), ts.fullFnNames())) ;
		return new StringRepresentation(result, MediaType.TEXT_HTML, Language.valueOf("UTF-8"));
	}

}
