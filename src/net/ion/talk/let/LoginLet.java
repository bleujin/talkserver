package net.ion.talk.let;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.ion.framework.util.Debug;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.RandomUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import org.restlet.security.Verifier;

public class LoginLet implements IServiceLet{
	
	
	@Get
	public Representation login(@AnContext TreeContext context, @AnRequest InnerRequest req) throws Exception{
		final String userId = req.getChallengeResponse().getIdentifier();
		final String accessToken = new ObjectId().toString() ;

		RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
		ReadSession session = rentry.login();

        String wsUri = session.tranSync(new TransactionJob<String>() {
            @Override
            public String handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/" + userId).property("accessToken", accessToken);

                List<WriteNode> list = wsession.pathBy("/servers").children().toList();
                Collections.shuffle(list);
                WriteNode firstNode = list.get(0);
                return "ws://" + firstNode.property("host").stringValue() + ":" + firstNode.property("port").stringValue() + "/websocket/" + userId + "/" + accessToken;
            }
        });

        return new StringRepresentation(wsUri);

	}
}
