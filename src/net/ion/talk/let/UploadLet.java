package net.ion.talk.let;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.ion.framework.file.PropFile;
import net.ion.framework.file.PropFileManager;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.nradon.let.ServiceLetUtil;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.representation.JsonObjectRepresentation;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.CharacterSet;
import org.restlet.data.Disposition;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class UploadLet implements IServiceLet {

	@Get
	public Representation viewFile(
			@AnContext TreeContext context, @AnRequest InnerRequest request, 
			@PathParam("fieldname") String fieldname) throws IOException {

		if (StringUtil.isBlank(fieldname)){  // dir info
			JsonArray json = new JsonArray() ;
			List<PropFile> dirFiles = createFileManager(context).listFile(resourcePath(request)) ;
			for (PropFile pfile : dirFiles) {
				JsonObject eleJson = JsonObject.fromString(pfile.propJson()) ;
				json.add(eleJson) ;
			}
			
			return new StringRepresentation(json.toString(), MediaType.TEXT_ALL, Language.ALL, CharacterSet.UTF_8) ; 
		}
		
		
		PropFile propFile = createFileManager(context).read(fieldResourcePath(request));
		if (! propFile.exist())
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "filePath:" + propFile.filePath()) ;

		JsonObject props = JsonObject.fromString(propFile.propJson()) ; 
		InputRepresentation result = new InputRepresentation(propFile.inputStream()) ;
		Disposition disposition = new Disposition(Disposition.TYPE_ATTACHMENT) ;
		String localName = ServiceLetUtil.isExplorer(request) ? URLEncoder.encode(props.asString("filename"), "UTF-8") : new String(props.asString("filename").getBytes("utf-8"), "latin1") ;
		disposition.setFilename(localName) ;
		result.setDisposition(disposition) ;
		result.setMediaType(MediaType.valueOf(props.asString("content-type"))) ;
		
		return result ;
	}

	@Delete
	public Representation deleteResource(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException, ResourceException  {
		PropFile propFile = createFileManager(context).read(fieldResourcePath(request));
		
		boolean exist = propFile.removeFile() ;
		if (!exist)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "filePath:" + propFile.filePath()) ;
		return new StringRepresentation("{removed:true}") ;
	}

	@Put
	public Representation uploadFile(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException, FileUploadException, ResourceException  {
		return createFile(context, request) ;
	}

	// Create.
	@Post
	public Representation createFile(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException, FileUploadException, ResourceException {
		final Representation entity = request.getEntity();
		if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)){ // put mutation
			JsonObject jso = saveFile(context, request);
			return new JsonObjectRepresentation(jso) ;
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Multipart/form-data required.[" + entity.getMediaType() + "]");
	}
	
	private JsonObject saveFile(TreeContext context, InnerRequest request) throws IOException, FileNotFoundException {
		JsonObject result = JsonObject.create() ;
		JsonObject paramJson = JsonObject.create() ;
		Set<Entry> entrySet = request.getFormParameter().entrySet();
		for (Entry<String, Object> entry : entrySet) {
			if (! (entry.getValue() instanceof FileItem)){
				result.put(entry.getKey(), ObjectUtil.toString(entry.getValue())) ;
				paramJson.put(entry.getKey(), ObjectUtil.toString(entry.getValue())) ;
			}
		}
		
		for (Entry<String, Object> entry : entrySet) {
			if (entry.getValue() instanceof FileItem) { 
				FileItem fitem = (FileItem) entry.getValue() ;
				if (fitem.getSize() <= 0) continue ;  
				
				String filePath = resourcePath(request) + "/" + entry.getKey();
				JsonObject fileJsonItem = JsonObject.create() ;
				fileJsonItem.put("size", fitem.getSize());
				fileJsonItem.put("content-type", fitem.getContentType());
				fileJsonItem.put("filename", URLDecoder.decode(fitem.getName(), "UTF-8"));
				fileJsonItem.put("fieldname", fitem.getFieldName());
				fileJsonItem.put("param", paramJson) ;
				fileJsonItem.put("filePath", filePath) ;
				
				result.add(entry.getKey(), fileJsonItem) ;
				createFileManager(context).save(filePath, fileJsonItem, fitem.getInputStream()) ;
			}
		}
		
		return result ;
	}

	private String resourcePath(InnerRequest request) {
		return StringUtil.reverse(request.getAttribute("userId")) + "/" + request.getAttribute("resource");
	}
	
	private String fieldResourcePath(InnerRequest request){
		return resourcePath(request) + "/" + request.getAttribute("fieldname") ; 
	}

	private PropFileManager createFileManager(TreeContext context) {
		String baseDirName = context.getAttributeObject("base.dir", "./resource/temp", String.class);
		PropFileManager pfm = PropFileManager.create(baseDirName) ;
		return pfm;
	}
}
