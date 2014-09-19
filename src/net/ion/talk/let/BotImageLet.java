package net.ion.talk.let;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import net.ion.radon.core.let.FileResponseBuilder;

@Path("/bot")
public class BotImageLet {

	@GET
	@Path("/icon/{botId}.jpg")
	public Response viewWebImage(@PathParam("botId") String botId){
		
		File file = new File("./resource/bot/" + botId + "_web.jpg") ;
		if (! file.exists()) {
			file = new File("./resource/bot/unknown.jpg") ;
		}
		return new FileResponseBuilder(file).build() ;
	}

	@GET
	@Path("/bimage/{botId}/{remain : .*}")
	public Response viewWebImage(@PathParam("botId") String botId, @PathParam("remain") String remainPath){
		
		File file = new File("./resource/bot/" + botId + "/" + remainPath) ;
		if (! file.exists()) {
			file = new File("./resource/bot/unknown.jpg") ;
		}
		return new FileResponseBuilder(file).build() ;
	}
}
