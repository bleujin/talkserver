package net.ion.talk.let;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.aclient.ResourceException;
import net.ion.radon.core.TreeContext;
import net.ion.talk.misc.AmzS3Service;
import net.ion.talk.misc.S3Helper;
import net.ion.talk.misc.S3PropFile;

import org.apache.commons.fileupload.FileUploadException;
import org.jboss.resteasy.plugins.providers.multipart.FormDataHandler;
import org.jboss.resteasy.plugins.providers.multipart.InputBody;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;


@Path("")
public class S3UploadLet  {

	@GET
	@Path("/{userId}/{resource}")
	public Response viewFileList(@Context TreeContext context, @PathParam("userId") String userId, @PathParam("resource") String resource) throws IOException, ServiceException {
		JsonArray json = new JsonArray() ;
		List<S3PropFile> dirFiles = creates3Helper(context).listFile(resourcePath(userId, resource)) ;
		for (S3PropFile pfile : dirFiles) {
			JsonObject eleJson = JsonObject.fromString(pfile.propJson()) ;
			json.add(eleJson) ;
		}
		return Response.ok(json, javax.ws.rs.core.MediaType.APPLICATION_JSON).language("UTF-8").build() ;
	}

	@GET
	@Path("/{userId}/{resource}/{fieldname}")
	public Response viewFile(@Context TreeContext context, @PathParam("userId") String userId, @PathParam("resource") String resource, @PathParam("fieldname") String fieldname, @HeaderParam("User-Agent") String userAgent) throws IOException, ServiceException {
		S3PropFile propFile = creates3Helper(context).read(fieldResourcePath(userId, resource, fieldname));
		if (! propFile.exist())
			return Response.status(Status.NOT_FOUND).build() ;

		JsonObject props = JsonObject.fromString(propFile.propJson()) ; 
		boolean isExplorer = userAgent.indexOf("MSIE") > -1;
		String localName = isExplorer ? URLEncoder.encode(props.asString("filename"), "UTF-8") : new String(props.asString("filename").getBytes("utf-8"), "latin1");
		// disposition.setFilename(localName) ;
		// result.setDisposition(disposition) ;
		// result.setMediaType(MediaType.valueOf(props.asString("content-type"))) ;

		return Response.ok(propFile.inputStream(), MediaType.valueOf(props.asString("content-type"))).header("Content-Disposition", "attachment; filename=\"" + localName + "\"").build();
	}

	// Create.
	@POST
	@Path("/{userId}/{resource}") 
	@Consumes(MediaType.MULTIPART_FORM_DATA) 
	public JsonObject createFile(@Context TreeContext context, MultipartFormDataInput minput, @PathParam("userId") String userId, @PathParam("resource") String resource) throws IOException, FileUploadException, ResourceException {
		return saveFile(context, minput, resourcePath(userId, resource));
	}
	
	@DELETE
	@Path("/{userId}/{resource}/{fieldname}")
	protected String deleteResource(@Context TreeContext context, @PathParam("userId") String userId, @PathParam("resource") String resource, @PathParam("fieldname") String fieldname) throws IOException, ResourceException, S3ServiceException  {
		creates3Helper(context).remove(fieldResourcePath(userId, resource, fieldname));
		return "{removed:true}" ;
	}
	

	
	private JsonObject saveFile(final TreeContext context, MultipartFormDataInput mdinput, final String resourcePath) throws IOException, FileNotFoundException {
		final JsonObject result = new JsonObject() ;
		mdinput.dataHandle(new FormDataHandler<Void>() {
			@Override
			public Void handle(InputBody ibody) throws IOException {
				if (ibody.isFilePart()) {
					String filePath = resourcePath + "/" + ibody.name();
					JsonObject fileJsonItem = JsonObject.create();
					fileJsonItem.put("content-type", ibody.mediaType().toString());
					fileJsonItem.put("filename", ibody.filename());
					fileJsonItem.put("fieldname", ibody.name());
					fileJsonItem.put("filePath", filePath);
					creates3Helper(context).save(filePath, fileJsonItem, ibody.asStream(), ibody.mediaType().toString()) ;
					result.add(ibody.name(), fileJsonItem);
				} else {
					result.put(ibody.name(), ibody.asString());
				}
				return null ;
			}
		});
		
		return result ;
	}

	private String resourcePath(String userId, String resource) {
		return userId + "/" + resource;
	}
	
	private String fieldResourcePath(String userId, String resource, String fieldname){
		return resourcePath(userId, resource) + "/" + fieldname ; 
	}

	private S3Helper creates3Helper(TreeContext context) {
		S3Helper helper = new S3Helper(context.getAttributeObject(AmzS3Service.class.getCanonicalName(), AmzS3Service.class)) ;
		return helper;
	}
	
	
	
}


