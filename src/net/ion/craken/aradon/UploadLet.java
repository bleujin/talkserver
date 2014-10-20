package net.ion.craken.aradon;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.IOUtil;
import net.ion.radon.core.ContextParam;

import org.jboss.resteasy.plugins.providers.multipart.InputBody;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
 

@Path("/upload")
public class UploadLet {

	@POST
	@Path("")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String upsert(@ContextParam("repository") RepositoryEntry repository, MultipartFormDataInput minput) throws Exception {
		
		 // @FormParam("path") final String nodePath, @FormParam("uploadFile") final FileItem fileItem 
		Map<String, List<InputPart>> mmap = minput.getFormDataMap() ;
		String nodePath = InputBody.create("path", mmap.get("path").get(0)).asString() ;
		InputStream input = InputBody.create("uploadFile", mmap.get("uploadFile").get(0)).asStream() ;
		
		writeBlob(repository, nodePath, input);
		IOUtil.toStringWithClose(input) ;
		return new String("{\"success\":true}");
	}

	private void writeBlob(RepositoryEntry rentry, final String nodePath, final InputStream input) throws IOException, Exception {
		ReadSession session = rentry.login();
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy(nodePath).blob("blob", input);
				return null;
			}
		}, InternalServerErrorHandler.DEFAULT);
	}

}
