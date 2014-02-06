package net.ion.talk;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectId;

public abstract class TalkMessage {

	public static TalkMessage fromJsonString(String json) {
		try {
			return new TalkScriptMessage(JsonObject.fromString(json));
		} catch (Exception notJson) {
			return new PlainTalkMessage(json);
		}
	}

	public static TalkMessage fromScript(String script) {
		return new TalkScriptMessage(new JsonObject().put("script", script).put("id", new ObjectId().toString()).put("params", new JsonObject())) ;
	}

	public abstract String script() ;

	public abstract String id();

	public abstract JsonObject params();

	public abstract String toPlainMessage() ;
}


class TalkScriptMessage extends TalkMessage {
	
	private JsonObject json;
	
	TalkScriptMessage(JsonObject json) {
		this.json = json;
	}

	public static TalkMessage fromScript(String script) {
		return new TalkScriptMessage(new JsonObject().put("script", script).put("id", new ObjectId().toString()).put("params", new JsonObject())) ;
	}

	public String script() {
		return json.asString("script");
	}

	public String id() {
		return json.asString("id");
	}

	public JsonObject params() {
		return json.asJsonObject("params");
	}

	public String toPlainMessage() {
		return json.toString() ;
	}
	
}



class PlainTalkMessage extends TalkMessage{

	private String msg;

	public PlainTalkMessage(String msg) {
		this.msg = msg ;
	}

	@Override
	public String id() {
		throw new UnsupportedOperationException("this plain msg") ;
	}

	@Override
	public JsonObject params() {
		throw new UnsupportedOperationException("this plain msg") ;
	}

	@Override
	public String script() {
		throw new UnsupportedOperationException("this plain msg") ;
	}

	@Override
	public String toPlainMessage() {
		return msg;
	}
	
}
