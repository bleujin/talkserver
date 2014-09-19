package net.ion.talk.let;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.radon.core.TreeContext;
import net.ion.talk.ParameterMap;
import net.ion.talk.misc.ScriptTemplate;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import net.ion.talk.script.ScriptResponseHandler;
import net.ion.talk.script.TalkScript;

import org.jboss.resteasy.spi.HttpRequest;

@Path("")
public class ScriptExecLet {

	@GET
	@Path("/script")
	@Deprecated
	public String viewScript(@Context TreeContext context) throws IOException {
		TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);
		ReadSession session = ts.session() ;
		ReadNode node = session.root() ;
		
		String result = node.transformer(new ScriptTemplate(session.workspace().parseEngine(), ts.fullFnNames(), "viewscript.tpl")) ;
		return result ;
//		return new StringRepresentation(result, MediaType.TEXT_HTML, Language.valueOf("UTF-8"));
	}
	
	@POST
	@Path("/execute/{packName}/{fnName : [A-Za-z0-9_]*}.{returnType}")
	public Response execute(@Context TreeContext context, @Context HttpRequest request, @PathParam("packName") String packName, @PathParam("fnName") String fnName, @PathParam("returnType") String returnType) {
        TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);

        String fnNames = packName + "/" + fnName;
        ParameterMap params = ParameterMap.create(request) ;
        
        return ts.callFn(fnNames, params, makeDefaultResponseHandler(returnType)) ;
	}
	
	
	
	
	public final static ScriptResponseHandler<JsonElement> jsonHandler(){
		return new ScriptResponseHandler<JsonElement>() {
			@Override
			public JsonElement onSuccess(String fullName, ParameterMap pmap, Object result) {
	        	return TalkResponseBuilder.makeResponse(fullName, result).toJsonElement() ;
			}
			
			@Override
			public JsonElement onThrow(String fullFnName, ParameterMap params, Exception ex) {
                return TalkResponseBuilder.failResponse(ex).toJsonElement();
			}
		} ;
	}
	
	public final static ScriptResponseHandler<String> stringHandler(){
		return new ScriptResponseHandler<String>() {
			@Override
			public String onSuccess(String fullName, ParameterMap pmap, Object result) {
	        	return TalkResponseBuilder.makeResponse(fullName, result).toString();
			}
			
			@Override
			public String onThrow(String fullFnName, ParameterMap params, Exception ex) {
                return TalkResponseBuilder.failResponse(ex).toString();
			}
		} ;
	}
	
	
	public final static ScriptResponseHandler<Response> makeDefaultResponseHandler(final String returnType){
		return new ScriptResponseHandler<Response>() {
			@Override
			public Response onSuccess(String fullName, ParameterMap pmap, Object result) {
		        if ("json".equals(returnType)){
		        	return Response.ok(TalkResponseBuilder.makeResponse(fullName, result).toJsonObject(), MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
		        } else if("string".equals(returnType)) {
		        	return Response.ok(TalkResponseBuilder.makeResponse(fullName, result).toString(), MediaType.TEXT_PLAIN + "; charset=UTF-8").build();
		        } else {
		        	return Response.ok((InputStream) result, MediaType.APPLICATION_OCTET_STREAM).build();
		        }
			}
			
			@Override
			public Response onThrow(String fullFnName, ParameterMap params, Exception ex) {
                return Response.ok(TalkResponseBuilder.failResponse(ex).toJsonObject(), MediaType.APPLICATION_JSON).build() ;
			}
		} ;
	}
	
}
