package net.ion.talk.bot.connect;

import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.talk.script.BotMessage;

public interface BotCompletionHandler {
	public void onCompleted(ReadSession session, BotMessage bm, JsonObject response);
	public void onThrowable(Throwable t);
}
