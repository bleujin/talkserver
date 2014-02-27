package net.ion.talk.filter;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.SecretVerifier;
import org.restlet.security.User;
import org.restlet.security.Verifier;

public class ToonVerifier  extends SecretVerifier {

	private ReadSession rsession;
	ToonVerifier(ReadSession rsession) {
		this.rsession = rsession ;
	}

	public int verify(Request request, Response response) {
		int result = Verifier.RESULT_VALID;

		if (request.getChallengeResponse() == null) {
			result = 0;
		} else {
			String identifier = getIdentifier(request, response);
			char secret[] = getSecret(request, response);
			try {
				if (verify(identifier, secret) == 4)
					request.getClientInfo().setUser(new User(identifier));
				else
					result = -1;
			} catch (IllegalArgumentException iae) {
				result = Verifier.RESULT_UNKNOWN;
			}
		}
		return result;
	}

	public int verify(String identifier, char secret[]) {
		return compare(secret, getLocalSecret(identifier)) ? 4 : -1;
	}

	
	public char[] getLocalSecret(String identifier){
		ReadNode userNode = rsession.ghostBy("/users/" + identifier);
		if (userNode.isGhost()) return new char[0] ;
		
		return userNode.property(net.ion.talk.bean.Const.User.Password).stringValue().toCharArray() ;
	 }
}