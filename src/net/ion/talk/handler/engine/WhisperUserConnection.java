package net.ion.talk.handler.engine;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.talk.UserConnection;

import com.google.common.cache.Cache;

public class WhisperUserConnection extends UserConnection {

	private Cache<String, String> messageCache;

	public WhisperUserConnection(Cache<String, String> messageCache, UserConnection uconn) {
		super(uconn.inner());
		this.messageCache = messageCache;
	}

	public void sendMessage(String message) {
		JsonObject json = JsonObject.fromString(message);

		String messageId = json.asJsonObject("result").asString("messageId");
		messageCache.put(messageId, message);

		if (json.asJsonObject("result").has("svgUrl"))
			super.sendMessage(json.toString());
		else {
			json.asJsonObject("result").put("svgUrl", "/svg/command/" + messageId + "?botId=");
			super.sendMessage(json.toString());
		}
		// json.asJsonObject("result").put("svg", "<svg width='100%' height='25' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'><text x='5' y ='10' fill='navy' font-size='12'>"
		// + JsonUtil.findSimpleObject(json, "result/message") + "</text></svg>") ;
		// super.sendMessage(json.toString());
	}

}