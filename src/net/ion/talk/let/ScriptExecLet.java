package net.ion.talk.let;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.AnResponse;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.MultiValueMap;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.script.rhino.ResponseHandler;
import net.ion.script.rhino.RhinoScript;
import net.ion.talk.ParameterMap;

import net.ion.talk.ToonServer;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import org.mozilla.javascript.NativeJavaObject;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

public class ScriptExecLet implements IServiceLet {

	@Post
	public Representation execute(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException{
        RhinoEntry rengine = context.getAttributeObject(RhinoEntry.EntryName, RhinoEntry.class);

        String spath;
        String format;
        String scriptId;
        Object result;
        try {
            String[] splitPath = StringUtil.split(request.getPathReference().getPath(), ".");
            spath = splitPath[0];
            format = splitPath[1];
            scriptId = request.getFormParameter().get("id").toString();
        } catch (ArrayIndexOutOfBoundsException e1){
            e1.printStackTrace();
            throw new ResourceException(400);
        } catch (NullPointerException e2){
            e2.printStackTrace();
            throw new ResourceException(400);
        }

        try {
            if(spath.equals("/ajax")){
                String script = request.getFormParameter().get("script").toString();
                result = rengine.executeScript(scriptId, script, ParameterMap.create(request.getFormParameter()));
            }else
                result = rengine.executePath(scriptId, "/script"+spath, ParameterMap.create(request.getFormParameter()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            result = TalkResponseBuilder.makeResponse(e);
        }

        if(format.equals("json"))
            return new JsonObjectRepresentation(result);
        else if(format.equals("string"))
            return new StringRepresentation(result.toString());
        else
            return new InputRepresentation((InputStream) result);

	}

}
