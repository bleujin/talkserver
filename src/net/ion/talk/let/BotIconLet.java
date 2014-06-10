package net.ion.talk.let;

import java.io.File;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;

public class BotIconLet implements IServiceLet {

	
	@Get
	public Representation viewWebImage(@AnRequest InnerRequest request, @PathParam("botId") String botId){
		
		File file = new File("./resource/bot/" + botId + "_web.jpg") ;
		if (! file.exists()) {
			file = new File("./resource/bot/unknown.jpg") ;
		}
		return new FileRepresentation(file, MediaType.IMAGE_JPEG) ;
	}
}
