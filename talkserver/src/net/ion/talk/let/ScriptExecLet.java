package net.ion.talk.let;

import java.io.IOException;
import java.util.Map.Entry;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.MultiValueMap;
import net.ion.script.rhino.ResponseHandler;
import net.ion.script.rhino.RhinoScript;
import net.ion.talk.ParameterMap;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class ScriptExecLet implements IServiceLet {

	@Get
	public String helloWorld(@AnRequest InnerRequest request){
		StringBuilder result = new StringBuilder() ;
		MultiValueMap map = request.getFormParameter();
		
		for (Entry<String, Object> entry : map.entrySet()) {
			result.append(entry.getKey() + ":" + entry.getValue() + "\n") ;
		}

		return result.toString() ;
	}
	
	@Post
	public Representation execute(@AnContext TreeContext context, @AnRequest InnerRequest request,
                                  @PathParam("path") String spath, @PathParam("format") String format) throws IOException{
		
		RepositoryEntry r = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		ReadSession rsession = r.login();
		RhinoEntry rengine = context.getAttributeObject(RhinoEntry.EntryName, RhinoEntry.class);
        String script = rsession.pathBy("/script/"+spath).property("script").stringValue();

		RhinoScript rscript = rengine.newScript(spath).defineScript(script);
		rscript.bind("session", rsession).bind("params", ParameterMap.create(request.getFormParameter())) ;

		String scriptResult = rscript.exec(ResponseHandler.StringMessage);

		return new StringRepresentation(scriptResult) ;
	}
}
