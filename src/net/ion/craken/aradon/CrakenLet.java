package net.ion.craken.aradon;

import java.io.IOException;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Function;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.ContextParam;

@Path("/craken")
public class CrakenLet {

	private ReadSession rsession;
	private Engine engine;

	public CrakenLet(@ContextParam("repository") RepositoryEntry rentry) throws IOException{
		this.rsession = rentry.login() ;
		this.engine = rsession.workspace().parseEngine();
	}

	@GET
	@Path("")
	@Produces(MediaType.TEXT_HTML)
	public String rootExprore() throws IOException{
		return htmlExprore("/") ;
	}

	
	@GET
	@Path("{remain:.*}")
	@Produces(MediaType.TEXT_HTML)
	public String htmlExprore(@PathParam("remain") String path) throws IOException{
		
		ReadNode find = rsession.ghostBy(path) ;
		if (find.isGhost()) return "not found node" ;
		
		String result = engine.transform(IOUtil.toStringWithClose(getClass().getResourceAsStream("craken.tpl")), MapUtil.<String, Object>create("self", find)) ;
		
		return result ;
	}
}
