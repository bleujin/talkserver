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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.ion.framework.file.PropFile;
import net.ion.framework.file.PropFileManager;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.ResourceException;
import net.ion.radon.core.TreeContext;

import org.apache.commons.fileupload.FileUploadException;
import org.jboss.resteasy.plugins.providers.multipart.FormDataHandler;
import org.jboss.resteasy.plugins.providers.multipart.InputBody;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/upload")
public class UploadLet {

	private PropFileManager fm;
	public UploadLet(@Context TreeContext context){
		this.fm = createFileManager(context) ;
	}
	
	@GET
	@Path("/{userId}/{resource}")
	public Response viewFileList(@PathParam("userId") String userId, @PathParam("resource") String resource) throws IOException {
		JsonArray json = new JsonArray();
		List<PropFile> dirFiles = fm.listFile(resourcePath(userId, resource));
		for (PropFile pfile : dirFiles) {
			JsonObject eleJson = JsonObject.fromString(pfile.propJson());
			json.add(eleJson);
		}

		return Response.ok(json.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Path("/{userId}/{resource}/{fieldname}")
	public Response viewFile(@PathParam("userId") String userId, @PathParam("resource") String resource, @PathParam("fieldname") String fieldname, @HeaderParam("User-Agent") String userAgent) throws IOException {

		if (StringUtil.isBlank(fieldname)) { // dir info
			JsonArray json = new JsonArray();
			List<PropFile> dirFiles = fm.listFile(resourcePath(userId, resource));
			for (PropFile pfile : dirFiles) {
				JsonObject eleJson = JsonObject.fromString(pfile.propJson());
				json.add(eleJson);
			}

			return Response.ok(json.toString(), MediaType.APPLICATION_JSON_TYPE).build();
		}

		PropFile propFile = fm.read(fieldResourcePath(userId, resource, fieldname));
		if (!propFile.exist())
			return Response.status(Status.NOT_FOUND).build();

		JsonObject props = JsonObject.fromString(propFile.propJson());

		// Representation result = new InputRepresentation(propFile.inputStream()) ;
		//
		// Disposition disposition = new Disposition(Disposition.TYPE_ATTACHMENT) ;
		boolean isExplorer = userAgent.indexOf("MSIE") > -1;
		String localName = isExplorer ? URLEncoder.encode(props.asString("filename"), "UTF-8") : new String(props.asString("filename").getBytes("utf-8"), "latin1");
		// disposition.setFilename(localName) ;
		// result.setDisposition(disposition) ;
		// result.setMediaType(MediaType.valueOf(props.asString("content-type"))) ;

		return Response.ok(propFile.inputStream(), MediaType.valueOf(props.asString("content-type"))).header("Content-Disposition", "attachment; filename=\"" + localName + "\"").build();
	}

	@DELETE
	@Path("/{userId}/{resource}/{fieldname}")
	public Response deleteResource(@PathParam("userId") String userId, @PathParam("resource") String resource, @PathParam("fieldname") String fieldname) throws IOException, ResourceException {
		PropFile propFile = fm.read(fieldResourcePath(userId, resource, fieldname));

		boolean exist = propFile.removeFile();
		if (!exist)
			return Response.status(Status.NOT_FOUND).build();
		return Response.ok().entity("{removed:true}").build();
	}

	@PUT
	@Path("/{userId}/{resource}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JsonObject uploadFile(MultipartFormDataInput mdinput, @PathParam("userId") String userId, @PathParam("resource") String resource) throws IOException, FileUploadException, ResourceException {
		return saveFile(mdinput, resourcePath(userId, resource));
	}

	// Create.
	@POST
	@Path("/{userId}/{resource}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public JsonObject createFile(MultipartFormDataInput mdinput, @PathParam("userId") String userId, @PathParam("resource") String resource) throws IOException, FileUploadException, ResourceException {
		JsonObject jso = saveFile(mdinput, resourcePath(userId, resource));
		return jso;
	}

	private JsonObject saveFile(MultipartFormDataInput mdinput, final String resourcePath) throws IOException, FileNotFoundException {
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

					fm.save(filePath, fileJsonItem, ibody.asStream());
					result.add(ibody.name(), fileJsonItem);
				} else {
					result.put(ibody.name(), ibody.asString());
				}
				return null ;
			}
		});
		
		return result;
	}

	private String resourcePath(String userId, String resource) {
		return StringUtil.reverse(userId) + "/" + resource;
	}

	private String fieldResourcePath(String userId, String resource, String fieldname) {
		return resourcePath(userId, resource) + "/" + fieldname;
	}

	private PropFileManager createFileManager(TreeContext context) {
		String baseDirName = context.getAttributeObject("base.dir", "./resource/temp", String.class);
		PropFileManager pfm = PropFileManager.create(baseDirName);
		return pfm;
	}
}
