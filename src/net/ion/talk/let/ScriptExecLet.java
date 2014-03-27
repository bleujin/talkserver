package net.ion.talk.let;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
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

            //It will be create scriptId from server or client.
            scriptId = new ObjectId().toString();
//            scriptId = request.getFormParameter().get("id").toString();
        } catch (ArrayIndexOutOfBoundsException e1){
            e1.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        } catch (NullPointerException e2){
            e2.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }

        try {
            if(spath.equals("/ajax")){
                String script = request.getFormParameter().get("script").toString();
                result = rengine.executeScript(rentry.login(), scriptId, script, ParameterMap.create(request.getFormParameter()));
            }else{
                result = rengine.executePath(rentry.login(), scriptId, "/script"+spath, ParameterMap.create(request.getFormParameter()));
            }

            if(result instanceof Throwable)
                throw (Exception) result;

            if(format.equals("json"))
                return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(scriptId, result));
            else if(format.equals("string"))
                return new StringRepresentation(TalkResponseBuilder.makeResponse(scriptId, result).toString());
            else
                return new InputRepresentation((InputStream) result);


        } catch (Exception e) {
            e.printStackTrace();
            if(format.equals("json"))
                return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(e));
            else if(format.equals("string"))
                return new StringRepresentation(TalkResponseBuilder.makeResponse(e).toString());
            else
                return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(e));

        }
	}

}
