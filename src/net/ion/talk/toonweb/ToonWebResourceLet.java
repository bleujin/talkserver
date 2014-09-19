package net.ion.talk.toonweb;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.ion.framework.util.ObjectUtil;
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
			return new FileResponseBuilder(file).build() ;
		} else {
			return Response.status(Status.NOT_FOUND).build() ;
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

