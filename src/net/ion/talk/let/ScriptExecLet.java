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
import net.ion.talk.TalkScript;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

public class ScriptExecLet implements IServiceLet {

	@Post
	public Representation execute(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException{
        TalkScript ts = context.getAttributeObject(TalkScript.class.getCanonicalName(), TalkScript.class);

        String scriptId = new ObjectId().toString();
        String remainPath = request.getRemainPath() ;
        String fnName = StringUtil.substringBeforeLast(remainPath, ".");
        String returnType = StringUtil.substringAfterLast(remainPath, ".");
        ParameterMap params = ParameterMap.create(request.getFormParameter()) ;
        
        try {
	        Object result = ts.callFn(fnName, params) ;
	        
	        if ("json".equals(returnType)){
	        	return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
	        } else if("string".equals(returnType)) {
	        	return new StringRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
	        } else {
	        	return new InputRepresentation((InputStream) result);
	        }
        } catch(IOException e){
        	 if(returnType.equals("json"))
                 return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(e));
             else if(returnType.equals("string"))
                 return new StringRepresentation(TalkResponseBuilder.makeResponse(e));
             else
                 return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(e));
        }
        
//        String spath;
//        String format;
//        String scriptId;
//        Object result;
//
//        try {
//        	
//            String[] splitPath = StringUtil.split(request.getPathReference().getPath(), ".");
//            spath = splitPath[0];
//            format = splitPath[1];
//
//            scriptId = new ObjectId().toString();
//        } catch (ArrayIndexOutOfBoundsException e1){
//            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e1);
//        } catch (NullPointerException e2){
//            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e2);
//        }
//
//        try {
//            result = executeScript(request, ts, spath, scriptId);
//
//            if(result instanceof Throwable)
//                throw (Exception) result;
//
//            return returnResult(format, scriptId, result);
//
//        } catch (Exception e) {
//            return returnException(format, e);
//        }
	}

    private Object executeScript(InnerRequest request, TalkScript rengine, String spath, String scriptId) throws IOException {
        Object result;
        if(spath.equals("/ajax")){
            String fname = request.getFormParameter().get("fname").toString();
            result = rengine.callFn(fname, ParameterMap.create(request.getFormParameter())) ;
        }else{
            result = rengine.callFn("/script"+spath, ParameterMap.create(request.getFormParameter()));
        }
        return result;
    }

    private Representation returnResult(String format, String scriptId, Object result) {

        if(format.equals("json"))
            return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
        else if(format.equals("string"))
            return new StringRepresentation(TalkResponseBuilder.makeResponse(scriptId, result).toString());
        else
            return new InputRepresentation((InputStream) result);
    }

    private Representation returnException(String format, Exception e) {
        e.printStackTrace();
        if(format.equals("json"))
            return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(e));
        else if(format.equals("string"))
            return new StringRepresentation(TalkResponseBuilder.makeResponse(e).toString());
        else
            return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(e));
    }

}
