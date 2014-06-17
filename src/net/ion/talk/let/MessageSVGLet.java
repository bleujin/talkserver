package net.ion.talk.let;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.script.ScriptException;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.emotion.Emotion;
import net.ion.emotion.EmotionalState;
import net.ion.emotion.Empathyscope;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.bean.Const;
import net.ion.talk.bot.LineCalculator;
import net.ion.talk.bot.ToonBot;
import net.ion.talk.handler.template.DummyPath;
import net.ion.talk.script.BotMessage;
import net.ion.talk.script.BotScript;

import org.apache.lucene.analysis.kr.utils.StringUtil;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import scala.sys.process.ProcessBuilderImpl.Dummy;

public class MessageSVGLet implements IServiceLet  {

    private static final int CHARACTER_HEIGHT = 90;

	@Get
	public Representation viewSVG(@ContextParam("repository") RepositoryEntry rentry, @AnRequest InnerRequest request, @AnContext TreeContext context, 
				@PathParam("roomId") String roomId, @PathParam("messageId") String messageId, @FormParam("type") @DefaultValue("sender") final String type, @FormParam("botId") String botId) throws IOException, ScriptException {
		
		ReadSession rsession = rentry.login() ;
		final ReadNode messageNode = rsession.ghostBy("/rooms/" + roomId + "/messages/" + messageId);
		String message = messageNode.isGhost() ? "not found message" : messageNode.property("message").asString() ;

		
		final Engine engine = rsession.workspace().parseEngine() ;

		final int lineNum = LineCalculator.linesOf(message);
        final int messageBodyHeight = lineNum * LineCalculator.PIXEL_PER_LINE;
        final int rectHeight = Math.max(messageBodyHeight, 90) ;
        final int fromWhoY = rectHeight + (LineCalculator.BUBBLE_PADDING * 2) + 10;
		
		Map<String, Object> map = MapUtil.chainKeyMap()
                .put("node", messageNode)
                .put("message", message)
                .put("rectHeight", rectHeight)
                .put("foreignObjectHeight", messageBodyHeight)
                .put("fromWhoY", fromWhoY)
                .toMap() ;

		ReadNode roomNode = rsession.pathBy("/rooms/" + roomId);
		if (roomNode.hasRef(Const.Bot.PostBot) && roomNode.refChildren(Const.Bot.PostBot).toList().size() > 0) {
			
			ReadNode postNode = roomNode.ref(Const.Bot.PostBot) ;
			String typePath = postNode.property(type).asString() ;
			String template = IOUtil.toStringWithClose(new FileInputStream(new File(typePath))) ;
			
			BotScript bs = context.getAttributeObject(BotScript.class.getCanonicalName(), BotScript.class) ;
			bs.callFrom(botId, "onPost", map, messageNode) ;
			
			String rendered = engine.transform(template, map) ;
			return new StringRepresentation(rendered, MediaType.IMAGE_SVG) ;
		}
		

		
		String template = "" ;
		if ("sender".equals(type)){
			template = IOUtil.toStringWithClose(DummyPath.class.getResourceAsStream("default_sender.tpl")) ;
		} else {
			template = IOUtil.toStringWithClose(DummyPath.class.getResourceAsStream("default_receiver.tpl")) ;
		}
		
        String result = engine.transform(template, map) ;
		return new StringRepresentation(result, MediaType.IMAGE_SVG) ;
	}
}
