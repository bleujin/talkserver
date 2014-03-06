package net.ion.talk.let;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.let.InnerRequest;

import org.apache.commons.io.FilenameUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
