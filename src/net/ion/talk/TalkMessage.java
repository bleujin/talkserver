package net.ion.talk;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public abstract class TalkMessage {

	public static TalkMessage fromJsonString(String jsonText) {
		try {
			JsonObject json = JsonObject.fromString(jsonText);
			return new TalkScriptMessage(json.asString("id"), json.asString("script"), ParameterMap.create(json.asJsonObject("params"))).plainMessage(jsonText);
		} catch (Exception notJson) {
			return new IllegalTalkMessage(jsonText);
		}
	}

	public static TalkMessage fromScript(String id, String scriptPath, ParameterMap params) {
		return new TalkScriptMessage(id, scriptPath, params).plainMessage(scriptPath) ;
	}

	public abstract String script() ;

	public abstract String id();

	public abstract ParameterMap params();

	public abstract String toPlainMessage() ;
	
	public abstract boolean isNormalMessage() ;
	
	public abstract String userMessage() ;
	
	public abstract boolean isCommandUserMessage() ;
}


class TalkScriptMessage extends TalkMessage {
	
	private final String scriptPath;
	private ParameterMap params;
	private final String id ;
	private String plainMessage;
	
	TalkScriptMessage(String id, String scriptPath, ParameterMap params) {
		this.id = id ;
		this.scriptPath = scriptPath;
		this.params = params ;
	}

	public String script() {
		return scriptPath;
	}

	public String id() {
        return id.toString();
	}

	public ParameterMap params() {
		return params ;
	}

	public TalkScriptMessage plainMessage(String plainMessage){
		this.plainMessage = plainMessage ;
		return this ;
	}
	
	public String toPlainMessage() {
		return plainMessage;
	}
	
	public String userMessage(){
		return params.asString("message") ;
	}
	
	public boolean isCommandUserMessage(){
		return userMessage().startsWith("/") ;
	}
	
	public boolean isNormalMessage() {
		return StringUtil.isNotBlank(id) && StringUtil.isNotBlank(scriptPath) ;
	}
}




class IllegalTalkMessage extends TalkMessage{

	private String msg;

	public IllegalTalkMessage(String msg) {
		this.msg = msg ;
	}

	@Override
	public String id() {
		throw new UnsupportedOperationException("this plain msg") ;
	}

	@Override
	public ParameterMap params() {
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

	public boolean isNormalMessage() {
		return false ;
	}

	public boolean isCommandUserMessage() {
		return false ;
	}
	public String userMessage() {
		return StringUtil.EMPTY ;
	}
}
