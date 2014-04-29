package net.ion.talk;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public abstract class TalkMessage {

	public static TalkMessage fromJsonString(String jsonText) {
		try {
			JsonObject json = JsonObject.fromString(jsonText);
			return new TalkScriptMessage(json.asString("id"), json.asString("script"), ParameterMap.create(json.asJsonObject("params"))).plainMessage(jsonText);
		} catch (Exception notJson) {
			return new PlainTalkMessage(jsonText);
		}
	}

	public static TalkMessage fromScript(String id, String scriptPath, ParameterMap params) {
		return new TalkScriptMessage(id, scriptPath, params).plainMessage(scriptPath) ;
	}

	public abstract String script() ;

	public abstract String id();

	public abstract ParameterMap params();

	public abstract String toPlainMessage() ;
	
	public abstract boolean isScript() ;

	public String successMesage(Object result) {
		return JsonObject.create().put("id", id()).put("status", "success").put("result", ObjectUtil.toString(result)).put("script", script()).toString() ;
	}

	public String failMesage(Exception ex) {
		return JsonObject.create().put("id", id()).put("status", "failure").put("result", ex.getMessage()).put("script", script()).toString() ;
	}
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
	
	public boolean isScript() {
		return StringUtil.isNotBlank(id) && StringUtil.isNotBlank(scriptPath) ;
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

	public boolean isScript() {
		return false ;
	}

}
