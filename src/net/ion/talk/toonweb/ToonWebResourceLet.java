package net.ion.talk.toonweb;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.restlet.FileMetaType;
import net.ion.radon.core.ContextParam;
import net.ion.radon.core.let.FileResponseBuilder;

@Path("/toonweb")
public class ToonWebResourceLet {

	@GET
	@Path("/")
	public Response indexFile(@ContextParam("resourceDir") String baseDir) throws IOException {
		final String dirHome = ObjectUtil.coalesce(baseDir, "./resource/toonweb/");
        File file = new File(dirHome, "index.html");
		if (file.exists()) {
			String mtype = FileMetaType.mediaType(file.getName()) ;
			ResponseBuilder rbuilder = Response.status(Status.OK).type(mtype).entity(file);
			
			if (file.getName().endsWith(".html") || file.getName().endsWith(".js") || file.getName().endsWith(".css")){
				rbuilder.language("UTF-8") ;
			}
			
			Debug.debug(file, file.getName().endsWith(".html"));
			
			return rbuilder.build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("not found web file : " + file.getCanonicalPath()).build() ;
		}
	}
	
	@GET
	@Path("/{remain:.*}")
	public Response deliverFile(@ContextParam("resourceDir") String baseDir, @DefaultValue("") @PathParam("remain") String resourceName) throws IOException {
		final String dirHome = ObjectUtil.coalesce(baseDir, "./resource/toonweb/");
        File file = new File(dirHome, resourceName);
		if (file.exists()) {
			return new FileResponseBuilder(file).build() ;
		} else {
			return Response.status(404).build() ;
		}
	}
}

