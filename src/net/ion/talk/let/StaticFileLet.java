package net.ion.talk.let;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
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
public class StaticFileLet implements IServiceLet {

	@Get
	public Representation deliverFile(@AnRequest InnerRequest request) throws IOException {

		final String resourceHome = "./resource/";
		
		File file = new File(resourceHome + request.getPathReference().getPath());
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(resourceHome + request.getPathReference().getPath());
			String extension = FilenameUtils.getExtension(request.getRemainPath());
			return new InputRepresentation(fis, request.getPathService().getAradon().getMetadataService().getMediaType(extension));
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND) ;
		}
	}

}
