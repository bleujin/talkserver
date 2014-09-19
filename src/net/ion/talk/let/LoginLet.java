package net.ion.talk.let;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.handler.authentication.BasicAuthenticationHandler;
import net.ion.radon.core.TreeContext;
import net.ion.talk.util.NetworkUtil;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/login")
public class LoginLet {
	
	
	@GET @POST
	public String login(@Context TreeContext context, @Context HttpRequest req) throws Exception{
		final String userId = ObjectUtil.toString(req.getAttribute(BasicAuthenticationHandler.USERNAME)) ;
		RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		ReadSession session = rentry.login();

		return targetAddress(session, userId);
	}

	public static String targetAddress(ReadSession session, final String userId) throws InterruptedException, ExecutionException  {
		final String accessToken = new ObjectId().toString() ;

        String wsUri = session.tran(new TransactionJob<String>() {
            @Override
            public String handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/" + userId).property("accessToken", accessToken);

                List<WriteNode> list = wsession.pathBy("/servers").children().toList();
                if (list.size() > 0) {
                	Collections.shuffle(list);
                	WriteNode firstNode = list.get(0);
                	return "ws://" + firstNode.property("host").stringValue() + ":" + firstNode.property("port").stringValue() + "/websocket/" + userId + "/" + accessToken;
                } else {
                	return NetworkUtil.wsAddress(9000, "/websocket/" + userId + "/" + accessToken);
                }
            }
        }).get();

        return wsUri;
	}
}
