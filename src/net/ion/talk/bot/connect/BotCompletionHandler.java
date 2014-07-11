package net.ion.talk.bot.connect;

import net.ion.framework.parse.gson.JsonElement;

public interface BotCompletionHandler<R> {
	public R onCompleted(JsonElement response);
	public R onThrowable(Throwable t);
}
