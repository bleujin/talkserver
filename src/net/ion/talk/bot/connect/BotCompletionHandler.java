package net.ion.talk.bot.connect;

import net.ion.framework.parse.gson.JsonObject;

public interface BotCompletionHandler<R> {
	public R onCompleted(JsonObject response);
	public R onThrowable(Throwable t);
}
