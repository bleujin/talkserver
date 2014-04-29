package net.ion.craken.aradon;

import net.ion.craken.node.TranExceptionHandler;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class InternalServerErrorHandler implements TranExceptionHandler {

	public final static InternalServerErrorHandler DEFAULT = new InternalServerErrorHandler() ;
	
	@Override
	public void handle(WriteSession wsession, TransactionJob tjob, Throwable ex) {
		throw new ResourceException(Status.valueOf(500), ex.getMessage());
	}

}
