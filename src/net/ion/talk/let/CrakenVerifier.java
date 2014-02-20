package net.ion.talk.let;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;

import net.ion.framework.util.Debug;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.User;
import org.restlet.security.Verifier;

public class CrakenVerifier implements Verifier {

	private ReadSession session;
	private CrakenVerifier(ReadSession session) {
		this.session = session ;
	}

	public static CrakenVerifier test(ReadSession session) throws Exception {
		return new CrakenVerifier(session).addUser("emanon", "emanon");
	}

	@Override
	public int verify(Request request, Response response) {

		if (request.getChallengeResponse() == null){
			return Verifier.RESULT_MISSING;
		}
        String id = request.getChallengeResponse().getIdentifier() ;
        String pushId = String.valueOf(request.getChallengeResponse().getSecret());
        if(!session.exists("/users/" + id))
            return Verifier.RESULT_INVALID;
        if(!session.pathBy("/users/" + id).property("pushId").stringValue().equals(pushId))
            return Verifier.RESULT_INVALID;

        return Verifier.RESULT_VALID;
	}

	// Only Use Test
	CrakenVerifier addUser(final String userId, final String pushId) throws Exception{
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/" + userId).property("pushId", pushId) ;
				return null;
			}
		}) ;
		return this;
	}
	
}
