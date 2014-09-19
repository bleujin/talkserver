package net.ion.talk.let;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.ion.framework.util.ObjectUtil;
import net.ion.radon.core.ContextParam;
import net.ion.radon.core.let.FileResponseBuilder;


@Path("/template")
public class ResourceLet {

	private String templateDir ;
	public ResourceLet(@ContextParam("templateDir") String templateDir){
		this.templateDir = templateDir ;
	}
	
	@GET
	@Path("/{remain : .*}")
	public Response deliverFile(@PathParam("remain") String remainPath) throws IOException {

		final String resourceHome = ObjectUtil.coalesce(templateDir, "./resource/template");
		File file = new File(resourceHome, remainPath);

		if (file.exists()) {
//			InputStream fis = new FileInputStream(file);
//			String extension = FilenameUtils.getExtension(path);
//			return new InputRepresentation(fis, request.getPathService().getAradon().getMetadataService().getMediaType(extension));
//			Response response = Response.created(file.toURI()).build();
			
//			return file ;
			
			return new FileResponseBuilder(file).build() ;
		} else {
			return Response.status(Status.NOT_FOUND).build() ;
		}
	}

}
