package net.ion.talk.let;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.spi.HttpRequest;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.mte.Engine;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.restlet.representation.Representation;
import net.ion.radon.core.ContextParam;
import net.ion.radon.core.TreeContext;
import net.ion.talk.bean.Const;
import net.ion.talk.handler.template.DummyPath;
import net.ion.talk.script.BotScript;

import com.google.common.cache.Cache;

@Path("/svg")
public class SVGLet {

	private static Engine ENGINE = Engine.createDefaultEngine() ; 

	
	@GET
	@Path("/message/{roomId}/{messageId}")
	@Produces("image/svg+xml")
	public String viewSVG(@ContextParam("repository") RepositoryEntry rentry, @Context TreeContext context, 
				@PathParam("roomId") String roomId, @PathParam("messageId") String messageId, @QueryParam("type") @DefaultValue("sender") final String type, @QueryParam("botId") String botId) throws IOException, ScriptException {
		
		ReadSession rsession = rentry.login() ;
		final ReadNode messageNode = rsession.ghostBy("/rooms/" + roomId + "/messages/" + messageId);
		String message = messageNode.isGhost() ? "not found message" : messageNode.property("message").asString() ;

		
		final Engine engine = rsession.workspace().parseEngine() ;

		Map<String, Object> map = MapUtil.chainKeyMap()
                .put("node", messageNode)
                .put("message", message)
                .toMap() ;

		ReadNode roomNode = rsession.pathBy("/rooms/" + roomId);

		if (roomNode.hasRef(Const.Bot.PostBot) && roomNode.refChildren(Const.Bot.PostBot).toList().size() > 0) {
			ReadNode postNode = roomNode.ref(Const.Bot.PostBot) ;
			String typePath = postNode.property(type).asString() ;
			String template = IOUtil.toStringWithClose(new FileInputStream(new File(typePath))) ;

			BotScript bs = context.getAttributeObject(BotScript.class.getCanonicalName(), BotScript.class) ;
			bs.callFrom(postNode.fqn().name(), "onPost", map, messageNode) ;

			String rendered = engine.transform(template, map) ;
			return rendered ;
		}

		String template = "" ;
		if ("sender".equals(type)){
			template = IOUtil.toStringWithClose(DummyPath.class.getResourceAsStream("default_sender.tpl")) ;
		} else {
			template = IOUtil.toStringWithClose(DummyPath.class.getResourceAsStream("default_receiver.tpl")) ;
		}
		
        String result = engine.transform(template, map) ;
		return result ;
	}
	
	
	
	@GET
	@Produces("image/svg+xml")
	@Path("/command/{messageId}")
	public String view(@Context TreeContext context, @PathParam("messageId") String messageId, @QueryParam("message") String message) throws ExecutionException, IOException{
		Cache<String, String> messageCache = context.getAttributeObject(Cache.class.getCanonicalName(), Cache.class) ;
		
		if ("0".equals(messageId)){
			return new String("<svg id='svg_0' width='200' height='20' xmlns='http://www.w3.org/2000/svg'><text id='msg_0' x='5' y ='10' fill='navy' font-size='11'>" + message + "</text></svg>") ;
		}
		
		String msgBody = messageCache.get(messageId, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "not found message";
			}
		}) ;
		
		String template = IOUtil.toStringWithClose(DummyPath.class.getResourceAsStream("whisper.tpl")) ;
		JsonObject json = JsonObject.fromString(msgBody) ;
		
		String rendered = ENGINE.transform(template, json.asJsonObject("result").toMap()) ;
		return rendered ;
	}

	
    private static final int CHARACTER_HEIGHT = 90;

	@GET
	@Path("/old/{roomId}/{type}/{messageId}")
	public Representation viewSVG(@ContextParam("repository") RepositoryEntry rentry, @Context HttpRequest request, 
				@PathParam("roomId") String roomId, @PathParam("type") String type, @PathParam("messageId") String messageId,
				@FormParam("charId") @DefaultValue("bat") String charId) throws IOException {
		
		ReadSession rsession = rentry.login() ;
		ReadNode messageNode = rsession.ghostBy("/rooms/" + roomId + "/messages/" + messageId);
		String message = messageNode.isGhost() ? "not found message" : messageNode.property("message").asString() ;

		
		
        /*  LineCalculator deleted. comment out following codes

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
		
		
		
		EmotionalState es = Empathyscope.feel(message) ;

		String template = "" ;
		if ("sender".equals(type)){
			template = IOUtil.toStringWithClose(ToonBot.class.getResourceAsStream("sender.tpl")) ;
		} else {
			template = IOUtil.toStringWithClose(ToonBot.class.getResourceAsStream("receiver.tpl")) ;
		}
		
		Engine engine = rsession.workspace().parseEngine();
		Emotion emotion = es.getStrongestEmotion();

        int characterY = rectHeight - CHARACTER_HEIGHT + 40;
        map.put("emotion", emotion.etype().toString().toLowerCase()) ;
        map.put("charId", StringUtil.defaultIfEmpty(charId, "bat")) ;
        map.put("characterY", characterY) ;

        
        

        String result = engine.transform(template, map) ;
		return new StringRepresentation(result, MediaType.IMAGE_SVG) ;
		*/
        return null;

	}
}
