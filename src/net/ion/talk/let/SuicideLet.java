package net.ion.talk.let;

import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;

import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.FormParam;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;

public class SuicideLet implements IServiceLet {

	@Get
	public String getMyName(@ContextParam("net.ion.talk.TalkEngine") TalkEngine engine) {
		return "Hello.. ServerID is " + engine.getConfig().name();
	}

	@Delete
	public String suicide(@AnContext TreeContext context, @DefaultValue("1") @FormParam("timeout") int timeout) {

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
