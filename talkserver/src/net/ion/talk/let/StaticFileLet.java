package net.ion.talk.let;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import org.apache.commons.io.FilenameUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.resource.Get;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Author: Ryunhee Han
 * Date: 2013. 12. 27.
 */
public class StaticFileLet implements IServiceLet {

    @Get
    public InputRepresentation deliverFile(@AnRequest InnerRequest request) throws IOException {

        File file = new File("./talkserver/resource/" + request.getPathReference().getPath());
        FileInputStream fis = null;
        if (file.exists()) {
            fis = new FileInputStream(
                    "./talkserver/resource/" + request.getPathReference().getPath());
            String extension = FilenameUtils.getExtension(request.getRemainPath());
            return new InputRepresentation(fis, request.getPathService().getAradon().getMetadataService().getMediaType(extension));
        } else {
            return new InputRepresentation(fis, MediaType.TEXT_HTML);
        }
    }

}
