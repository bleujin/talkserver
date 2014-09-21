package net.ion.talk;

import java.util.Map;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;

public abstract class TalkMessage {

	
	public enum MType {
		NORMAL, ILLEGAL, WHISPER
	}
	
	public static TalkMessage fromJsonString(String jsonText) {
		try {
			JsonObject json = JsonObject.fromString(jsonText);
			if (StringUtil.isBlank(json.asString("id")) || StringUtil.isBlank(json.asString("script"))) return new IllegalTalkMessage(jsonText) ; 
			
			return new TalkNormalMessage(json);
		} catch (Exception notJson) {
			return new IllegalTalkMessage(jsonText);
		}
	}

	public static TalkMessage fromTest(String id, String scriptPath, ParameterMap params) {
		return new TalkNormalMessage(id, scriptPath, params) ;
	}

	public abstract String scriptPath() ;

	public abstract String id();

	public abstract ParameterMap params();

	public abstract String toPlainMessage() ;
	
	public abstract MType messageType() ;
	
	public abstract String userMessage() ;
	
	public abstract TalkMessage setParam(String name, String value) ;

	public abstract TalkMessage resetParam(String name, String value) ;

}


class TalkNormalMessage extends TalkMessage {
	
	private final String scriptPath;
	private ParameterMap params;
	private final String id ;
	private String plainMessage;
	
	TalkNormalMessage(JsonObject json) {
		this.id = json.asString("id") ;
		this.scriptPath = json.asString("script") ;

		JsonObject paramJson = json.asJsonObject("params") ;
		this.params = ParameterMap.create(paramJson) ;
		this.plainMessage = json.toString() ;
	}

	TalkNormalMessage(String id, String scriptPath, ParameterMap params) {
		this.id = id ;
		this.scriptPath = scriptPath ;
		this.params = params;
		this.plainMessage = "only test" ;
	}

	public String scriptPath() {
		return scriptPath;
	}

	public String id() {
        return id.toString();
	}

	public ParameterMap params() {
		return params ;
	}

	public TalkNormalMessage plainMessage(String plainMessage){
		this.plainMessage = plainMessage ;
		return this ;
	}
	
	public String toPlainMessage() {
		return plainMessage;
	}
	
	public TalkNormalMessage setParam(String name, String value){
		params.set(name, value) ;
		return this ;
	}
	
	public TalkNormalMessage resetParam(String name, String value){
		params.reset(name, value) ;
		return this ;
	}
	
	public String userMessage(){
		String result = params.asString("message");
		if (result.startsWith("/")) return "@system " + result.substring(1) ; 
		return result ;
	}
	
	public MType messageType() {
		return userMessage().startsWith("@")? MType.WHISPER : MType.NORMAL ;
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
	public String scriptPath() {
		throw new UnsupportedOperationException("this plain msg") ;
	}

	@Override
	public String toPlainMessage() {
		return msg;
	}

	public MType messageType() {
		return MType.ILLEGAL ;
	}

	public String userMessage() {
		return StringUtil.EMPTY ;
	}
	
	public TalkMessage setParam(String name, String value) {
		return this ;
	}
	
	
	public TalkMessage resetParam(String name, String value) {
		return this ;
	}
}
