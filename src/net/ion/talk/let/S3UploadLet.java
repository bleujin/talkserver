package net.ion.talk.let;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.nradon.let.ServiceLetUtil;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.representation.JsonObjectRepresentation;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
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
import org.restlet.resource.ResourceException;

public class S3UploadLet implements IServiceLet  {

	// Create.
	@Post
	public Representation createFile(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException, FileUploadException, ResourceException {
		if (MediaType.MULTIPART_FORM_DATA.equals(request.getEntity().getMediaType(), true)){ // put mutation
			JsonObject jso = saveFile(context, request);
			return new JsonObjectRepresentation(jso) ;
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Multipart/form-data required.[" + request.getEntity().getMediaType() + "]");
	}
	
	@Get
	public Representation viewFile(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException, ServiceException {

		if (StringUtil.isBlank(request.getAttribute("fieldname"))){  // dir
			JsonArray json = new JsonArray() ;
			List<S3PropFile> dirFiles = creates3Helper(context).listFile(resourcePath(request)) ;
			for (S3PropFile pfile : dirFiles) {
				JsonObject eleJson = JsonObject.fromString(pfile.propJson()) ;
				json.add(eleJson) ;
			}
			
			return new StringRepresentation(json.toString(), MediaType.TEXT_ALL, Language.ALL, CharacterSet.UTF_8) ; 
		}
		
		
		S3PropFile propFile = creates3Helper(context).read(fieldResourcePath(request));
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
	protected Representation deleteResource(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException, ResourceException, S3ServiceException  {
		creates3Helper(context).remove(fieldResourcePath(request));
		return new StringRepresentation("{removed:true}") ;
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
				creates3Helper(context).save(filePath, fileJsonItem, fitem) ;
			}
				
		}
		
		return result ;
	}

	private String resourcePath(InnerRequest request) {
		return request.getAttribute("userId") + "/" + request.getAttribute("resource");
	}
	
	private String fieldResourcePath(InnerRequest request){
		return resourcePath(request) + "/" + request.getAttribute("fieldname") ; 
	}

	private S3Helper creates3Helper(TreeContext context) {
		S3Helper helper = new S3Helper(context.getAttributeObject(AmzS3Service.class.getCanonicalName(), AmzS3Service.class)) ;
		return helper;
	}
	
	
	
}


