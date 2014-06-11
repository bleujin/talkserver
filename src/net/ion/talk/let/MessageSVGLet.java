package net.ion.talk.let;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyValue;
import net.ion.emotion.EmotionalState;
import net.ion.emotion.Empathyscope;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.bot.ToonBot;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.lucene.analysis.kr.utils.StringUtil;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public class MessageSVGLet implements IServiceLet {

	@Get
	public Representation viewSVG(@ContextParam("repository") RepositoryEntry rentry, @AnRequest InnerRequest request, 
				@PathParam("roomId") String roomId, @PathParam("type") String type, @PathParam("messageId") String messageId,
				@FormParam("charId") @DefaultValue("bat") String charId) throws IOException {
		
		ReadSession rsession = rentry.login() ;
		ReadNode messageNode = rsession.ghostBy("/rooms/" + roomId + "/messages/" + messageId);
		String message = messageNode.isGhost() ? "not found message" : messageNode.property("message").asString() ;
		
		EmotionalState es = Empathyscope.feel(message) ;

		String template = "" ;
		if ("sender".equals(type)){
			template = IOUtil.toStringWithClose(ToonBot.class.getResourceAsStream("sender.tpl")) ;
		} else {
			template = IOUtil.toStringWithClose(ToonBot.class.getResourceAsStream("receiver.tpl")) ;
		}
		
		Engine engine = rsession.workspace().parseEngine();
		Map<String, Object> map = MapUtil.chainKeyMap().put("node", messageNode).put("message", message).put("charId", StringUtil.defaultIfEmpty(charId, "bat")).put("emotion", es.getStrongestEmotion().etype().toString().toLowerCase()).toMap() ;
		String result = engine.transform(template, map) ;
		

		return new StringRepresentation(result, MediaType.IMAGE_SVG) ;
	}
}
