package net.ion.talk.let;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.framework.parse.gson.JsonElement;
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

import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.io.InputStream;

public class ScriptExecLet implements IServiceLet {

	@Post
	public Representation execute(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException{
        RhinoEntry rengine = context.getAttributeObject(RhinoEntry.EntryName, RhinoEntry.class);
        RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class) ;

        String spath;
        String format;
        String scriptId;
        Object result;

        try {
            String[] splitPath = StringUtil.split(request.getPathReference().getPath(), ".");
            spath = splitPath[0];
            format = splitPath[1];

            scriptId = new ObjectId().toString();
        } catch (ArrayIndexOutOfBoundsException e1){
            e1.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        } catch (NullPointerException e2){
            e2.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }

        try {
            result = executeScript(request, rengine, rentry, spath, scriptId);

            if(result instanceof Throwable)
                throw (Exception) result;

            return returnResult(format, scriptId, result);

        } catch (Exception e) {
            return returnException(format, e);
        }
	}

    private Object executeScript(InnerRequest request, RhinoEntry rengine, RepositoryEntry rentry, String spath, String scriptId) throws IOException {
        Object result;
        if(spath.equals("/ajax")){
            String script = request.getFormParameter().get("script").toString();
            result = rengine.executeScript(rentry.login(), scriptId, script, ParameterMap.create(request.getFormParameter()));
        }else{
            result = rengine.executePath(rentry.login(), scriptId, "/script"+spath, ParameterMap.create(request.getFormParameter()));
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
