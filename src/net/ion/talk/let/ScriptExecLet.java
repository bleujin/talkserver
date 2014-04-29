package net.ion.talk.let;

import java.io.IOException;
import java.io.InputStream;

import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.talk.ParameterMap;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import net.ion.talk.script.ScriptExceptionHandler;
import net.ion.talk.script.ScriptSuccessHandler;
import net.ion.talk.script.TalkScript;

import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

public class ScriptExecLet implements IServiceLet {

	@Post
	public Representation execute(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException{
        TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);

        final String scriptId = new ObjectId().toString();
        String remainPath = request.getRemainPath() ;
        String fnName = StringUtil.substringBeforeLast(remainPath, ".");
        final String returnType = StringUtil.substringAfterLast(remainPath, ".");
        ParameterMap params = ParameterMap.create(request.getFormParameter()) ;
        
        return ts.callFn(fnName, params, 
        		new ScriptSuccessHandler<Representation>() {
					@Override
					public Representation success(Object result) {
				        if ("json".equals(returnType)){
				        	return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
				        } else if("string".equals(returnType)) {
				        	return new StringRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
				        } else {
				        	return new InputRepresentation((InputStream) result);
				        }
					}
				}, 
				new ScriptExceptionHandler<Representation>() {
					@Override
					public Representation ehandle(Exception ex, String fullFnName, ParameterMap params) {
		                return new JsonObjectRepresentation(TalkResponseBuilder.failResponse(ex));
					}
				}) ;
        
	}
}
