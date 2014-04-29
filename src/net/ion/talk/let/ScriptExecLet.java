package net.ion.talk.let;

import java.io.InputStream;

import net.ion.framework.util.StringUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.ParameterMap;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import net.ion.talk.script.ScriptResponseHandler;
import net.ion.talk.script.TalkScript;

import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public class ScriptExecLet implements IServiceLet {

	@Post
	public Representation execute(@AnContext TreeContext context, @AnRequest InnerRequest request) {
        TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);

        String remainPath = request.getRemainPath() ;
        String fnName = StringUtil.substringBeforeLast(remainPath, ".");
        final String returnType = StringUtil.substringAfterLast(remainPath, ".");
        ParameterMap params = ParameterMap.create(request.getFormParameter()) ;
        
        return ts.callFn(fnName, params, MakeDefaultResponseHandler(returnType)) ;
	}
	
	public final static ScriptResponseHandler<Representation> MakeDefaultResponseHandler(final String returnType){
		return new ScriptResponseHandler<Representation>() {
			@Override
			public Representation onSuccess(String fullName, ParameterMap pmap, Object result) {
		        if ("json".equals(returnType)){
		        	return TalkResponseBuilder.makeResponse(fullName, result).transformer(TalkResponse.ToJsonRepresentation);
		        } else if("string".equals(returnType)) {
		        	return TalkResponseBuilder.makeResponse(fullName, result).transformer(TalkResponse.ToStringRepresentation);
		        } else {
		        	return new InputRepresentation((InputStream) result);
		        }
			}
			
			@Override
			public Representation onThrow(String fullFnName, ParameterMap params, Exception ex) {
                return TalkResponseBuilder.failResponse(ex).transformer(TalkResponse.ToJsonRepresentation);
			}
		} ;
	}
	
}
