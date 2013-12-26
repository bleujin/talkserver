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

	public static CrakenVerifier test(ReadSession session) throws IOException {
		return new CrakenVerifier(session);
	}

	@Override
	public int verify(Request request, Response response) {
        Debug.line(request.getChallengeResponse());
        String id = request.getChallengeResponse().getIdentifier() ;
        return session.exists("/users/" + id) ? Verifier.RESULT_VALID : Verifier.RESULT_INVALID;
	}

	public CrakenVerifier addUser(final String userId, final String pwd) throws Exception{
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {


				wsession.pathBy("/users/" + userId).property("id", userId).property("pwd", pwd) ;
				return null;
			}
		}) ;
		return this;
	}
	
}
