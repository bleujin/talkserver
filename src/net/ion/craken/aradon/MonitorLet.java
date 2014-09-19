package net.ion.craken.aradon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;

import org.antlr.stringtemplate.StringTemplate;

@Path("/event")
public class MonitorLet {

	
	@GET
	@Path("/{remain: .*}")
	@Produces(javax.ws.rs.core.MediaType.TEXT_HTML)
	public String view(@PathParam("remain") String remainPath) throws FileNotFoundException, IOException{
		File tplFile = new File("./resource/toonweb/event.htm") ;
		final StringWriter template = new StringWriter();
		IOUtil.copyNClose( new FileReader(tplFile), template) ;
		
		StringTemplate st = new StringTemplate(template.toString());
		Map<String, String> configMap = MapUtil.<String>chainKeyMap().put("path", remainPath).toMap() ;
		
		st.setAttribute("config", configMap);
		
		return st.toString() ;
	}
}
