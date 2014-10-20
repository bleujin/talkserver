package net.ion.craken.aradon;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import net.ion.radon.core.ContextParam;
import net.ion.talk.ToonServer;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/misc")
public class MiscLet {

	@GET
	@Path("/shutdown")
	public String shutdown(@Context HttpRequest request,
			@DefaultValue("") @QueryParam("password") String password, 
			@DefaultValue("1000") @QueryParam("time") final int time, @ContextParam("net.ion.talk.ToonServer") final ToonServer server){
		
		if (! password.equals(server.config().serverConfig().password())) {
			return "not matched password" ;
		}
		
		new Thread(){
			public void run(){
				try {
					Thread.sleep(time);
					server.stop() ;
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}.start(); 
		
		return "bye after " + time;
	}
	
}
