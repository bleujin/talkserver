package net.ion.craken.aradon;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.Repository;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.representation.JsonObjectRepresentation;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

public class UploadLet implements IServiceLet {

	@Post
	public Representation upsert(@ContextParam("repository") Repository repository, @AnRequest Request request, @FormParam("workspace") String workspace, @FormParam("path") final String nodePath, @FormParam("uploadFile") final FileItem fileItem) throws Exception {

		if (isMultipartRequest(request)) {
			writeBlob(repository, workspace, nodePath, fileItem);
			return new JsonObjectRepresentation("{\"success\":true}");

		} else {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "No multipart request");
		}
	}

	private void writeBlob(Repository repository, String workspace, final String nodePath, final FileItem fileItem) throws IOException, Exception {
		ReadSession session = repository.login(workspace);
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy(nodePath).blob("blob", fileItem.getInputStream());
				return null;
			}
		}, InternalServerErrorHandler.DEFAULT);
	}

	private boolean isMultipartRequest(Request request) {
		return MediaType.MULTIPART_FORM_DATA.equals(request.getEntity().getMediaType(), true);
	}

}
