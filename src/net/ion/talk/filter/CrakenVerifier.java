package net.ion.talk.filter;

import java.util.concurrent.Executor;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.handler.authentication.PasswordAuthenticator;


public class CrakenVerifier implements PasswordAuthenticator {

	private ReadSession session;

	private CrakenVerifier(ReadSession session) {
		this.session = session;
	}

	public static CrakenVerifier test(ReadSession session) throws Exception {
		return new CrakenVerifier(session).addUser("emanon", "emanon");
	}

	// Only Use Test
	CrakenVerifier addUser(final String userId, final String pushId) throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/" + userId).property("pushId", pushId);
				return null;
			}
		});
		return this;
	}

	public void authenticate(HttpRequest request, String username, String password, ResultCallback callback, Executor handlerExecutor) {
		String expectedPassword = session.pathBy("/users/" + username).property("pushId").stringValue();
		if (expectedPassword != null && password.equals(expectedPassword)) {
			callback.success();
		} else {
			callback.failure();
		}
	}
}
