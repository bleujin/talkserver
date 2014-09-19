package net.ion.craken.aradon;

import javax.ws.rs.WebApplicationException;

import net.ion.craken.node.TranExceptionHandler;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;

public class InternalServerErrorHandler implements TranExceptionHandler {

	public final static InternalServerErrorHandler DEFAULT = new InternalServerErrorHandler() ;
	
	@Override
	public void handle(WriteSession wsession, TransactionJob tjob, Throwable ex) {
		throw new WebApplicationException(ex, javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
	}

}
