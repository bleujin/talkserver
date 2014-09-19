package net.ion.talk.let;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import net.ion.radon.core.ContextParam;
import net.ion.radon.core.TreeContext;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;

public class SuicideLet {

	@GET
	public String getMyName(@ContextParam("net.ion.talk.TalkEngine") TalkEngine engine) throws IOException {
		return "Hello.. ServerID is " + engine.readSession().workspace().repository().memberId();
	}

	@DELETE
	public String suicide(@Context TreeContext context, @DefaultValue("1") @QueryParam("timeout") int timeout) {

		long timeoutMili = Math.max(timeout, 100);

		final ToonServer server = context.getAttributeObject(ToonServer.class.getCanonicalName(), ToonServer.class);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					server.stop();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.exit(0);
				}
			}

		}, timeoutMili);

		return timeoutMili + "(ms) shutdown..";
	}
	
}
