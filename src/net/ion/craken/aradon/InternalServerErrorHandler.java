package net.ion.craken.aradon;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import net.ion.craken.node.TranExceptionHandler;
import net.ion.craken.node.WriteSession;

public class InternalServerErrorHandler implements TranExceptionHandler {

	public final static InternalServerErrorHandler DEFAULT = new InternalServerErrorHandler() ;
	
	@Override
	public void handle(WriteSession wsession, Throwable ex) {
		throw new ResourceException(Status.valueOf(500), ex.getMessage());
	}

}
