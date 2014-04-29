package net.ion.talk.let;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.let.InnerRequest;

import org.apache.commons.io.FilenameUtils;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Author: Ryunhee Han Date: 2013. 12. 27.
 */
public class ResourceLet implements IServiceLet {

	@Get
	public Representation deliverFile(@ContextParam("baseDir") String baseDir, @AnRequest InnerRequest request) throws IOException {

		final String resourceHome = ObjectUtil.coalesce(baseDir, "./resource/");


		File file = new File(resourceHome + request.getPathReference().getPath());

		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);
			String extension = FilenameUtils.getExtension(request.getRemainPath());
			return new InputRepresentation(fis, request.getPathService().getAradon().getMetadataService().getMediaType(extension));
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND) ;
		}
	}

}
