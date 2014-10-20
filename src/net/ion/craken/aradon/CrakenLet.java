package net.ion.craken.aradon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Function;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyValue;
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
	public Response rootExprore() throws IOException{
		return htmlExprore("/") ;
	}

	
	@GET
	@Path("/{remain: ^[^\\.]*$}")
	@Produces(MediaType.TEXT_HTML)
	public Response htmlExprore(@PathParam("remain") String path) throws IOException{
		ReadNode find = rsession.ghostBy(path) ;
		if (find.isGhost()) return Response.status(404).build() ;
		
		String result = engine.transform(IOUtil.toStringWithClose(getClass().getResourceAsStream("craken.tpl")), MapUtil.<String, Object>create("self", find)) ;
		return Response.ok(result).build() ;
	}
	
	
	@GET
	@Path("/{remain: .*}.{pid}")
	public Response property(@PathParam("remain") String path, @PathParam("pid") String pid) throws FileNotFoundException{
		ReadNode find = rsession.ghostBy(path) ;
		if (find.isGhost()) return Response.status(404).build() ;

		
		PropertyValue pvalue = find.property(pid) ;
		if (pvalue.isBlob()){
			InputStream input = pvalue.asBlob().toInputStream() ;
			return Response.ok(input, MediaType.APPLICATION_OCTET_STREAM).build() ;
		} else {
			return Response.ok(pvalue.asString()).build() ;
		}
	}
}
