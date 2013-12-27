package net.ion.talk.let;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.framework.util.Debug;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.AnResponse;
import net.ion.radon.core.let.InnerRequest;
import org.restlet.Response;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.io.IOException;

/**
 * Author: Ryunhee Han
 * Date: 2013. 12. 26.
 */
public class ScriptLet implements IServiceLet {

    @Get
    public StringRepresentation getPage(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException {
        RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
        ReadSession session = rentry.login("test");
        StringBuilder sb = new StringBuilder();
        String requestPath = "/script" + request.getPathReference().getPath();

        sb.append("<!DOCTYPE HTML><html><head></head><body><form method=\"POST\"><textarea style='height:400px; width: 600px;' name=\"script\">");
        if(session.exists(requestPath)){
            ReadNode node = session.pathBy(requestPath);
            sb.append(node.property("script").stringValue());
            sb.append("</textarea>");
            sb.append("<br/ ><input type=\"submit\" value=\"save\">");
            ReadChildren children = node.children();
            sb.append("<ul>");
            while(children.hasNext()){
                String childName = children.next().fqn().toString();
                sb.append("<li><a href=\"" + childName +"\">" + childName + "</a></li>");
            }
            sb.append("</ul>");
        }else{
            sb.append("</textarea>");
            sb.append("<br /><input type=\"submit\" value=\"save\">");
        }

        sb.append("</form>");
        sb.append("</body></html>");
        return new StringRepresentation(sb.toString(), MediaType.TEXT_HTML, Language.valueOf("UTF-8"));
    }


    @Post
    public StringRepresentation updatePage(@AnContext TreeContext context, @AnRequest InnerRequest request, @AnResponse Response response) throws Exception {

        final String requestPath = "/script" + request.getPathReference().getPath();
        RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
        ReadSession session = rentry.login("test");

        final String script = request.getFormParameter().getFirstValue("script").toString();

        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.createBy(requestPath).property("script", script);
                return null;
            }
        });

        response.redirectPermanent(requestPath);
        return null;
    }
}








