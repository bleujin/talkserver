package net.ion.talk.filter;

import java.util.concurrent.Executor;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.handler.authentication.PasswordAuthenticator;

public class ToonVerifier implements PasswordAuthenticator {

	private ReadSession rsession;

	public ToonVerifier(ReadSession rsession) {
		this.rsession = rsession;
	}

	@Override
	public void authenticate(HttpRequest request, String username, String password, ResultCallback callback, Executor handlerExecutor) {
		ReadNode userNode = rsession.ghostBy("/users/" + username);
		String expectedPassword = userNode.property(net.ion.talk.bean.Const.User.Password).stringValue();
		if (expectedPassword != null && password.equals(expectedPassword))
			callback.success();
		else
			callback.failure();
	}
}