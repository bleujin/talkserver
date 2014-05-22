package net.ion.craken.aradon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;

import org.antlr.stringtemplate.StringTemplate;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public class MonitorLet implements IServiceLet{

	@Get
	public Representation view(@AnRequest InnerRequest request) throws FileNotFoundException, IOException{
		File tplFile = new File("./resource/toonweb/event.htm") ;
		final StringWriter template = new StringWriter();
		IOUtil.copyNClose( new FileReader(tplFile), template) ;
		
		StringTemplate st = new StringTemplate(template.toString());
		Map<String, String> configMap = MapUtil.<String>chainKeyMap().put("path", request.getRemainPath()).toMap() ;
		
		st.setAttribute("config", configMap);
		
		return new StringRepresentation(st.toString(), MediaType.TEXT_HTML, Language.ALL);
	}
}
