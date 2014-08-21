package net.ion.talk.let;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import com.google.common.cache.Cache;

import net.ion.framework.mte.Engine;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;
import net.ion.talk.handler.engine.WhisperHandler;
import net.ion.talk.handler.template.DummyPath;

public class CommandSVGLet implements IServiceLet {

	private static Engine ENGINE = Engine.createDefaultEngine() ; 
	
	@Get
	public Representation view(@AnContext TreeContext context, @PathParam("messageId") String messageId, @FormParam("message") String message) throws ExecutionException, IOException{
		Cache<String, String> messageCache = context.getAttributeObject(Cache.class.getCanonicalName(), Cache.class) ;
		
		if ("0".equals(messageId)){
			return new StringRepresentation("<svg id='svg_0' width='200' height='20' xmlns='http://www.w3.org/2000/svg'><text id='msg_0' x='5' y ='10' fill='navy' font-size='11'>" + message + "</text></svg>", MediaType.IMAGE_SVG) ;
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
		return new StringRepresentation(rendered, MediaType.IMAGE_SVG) ;
	}
}
