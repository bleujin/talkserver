package net.ion.message.sms.callback;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import net.ion.framework.util.Debug;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/receive")
public class ConsoleCallbackLet {

	@POST
	public String printToConsole(@Context HttpRequest request) {
		MultivaluedMap<String, String> formParameter = request.getFormParameters() ;
		Debug.line(formParameter);
		return formParameter.toString();
	}

}
