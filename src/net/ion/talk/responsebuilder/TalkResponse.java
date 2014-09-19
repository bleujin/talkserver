package net.ion.talk.responsebuilder;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;

import com.google.common.base.Function;


public class TalkResponse {

    public static Function<JsonElement, JsonObject> FnJsonObject = new Function<JsonElement, JsonObject>(){
    	@Override
    	public JsonObject apply(JsonElement ele) {
    		return ele.getAsJsonObject();
    	}
    };
    public static Function<JsonElement, JsonArray> FnJsonArray = new Function<JsonElement, JsonArray>(){
    	@Override
    	public JsonArray apply(JsonElement ele) {
    		return ele.getAsJsonArray();
    	}
    };
    
    public static Function<JsonElement, Void> FnDebug = new Function<JsonElement, Void>(){
    	@Override
    	public Void apply(JsonElement ele) {
    		Debug.debug(ele);
    		return null ;
    	}
    };
    
    private JsonElement element;
    private TalkResponse(JsonElement element) {
        this.element = element;
    }

    public static TalkResponse create(JsonElement element){
        return new TalkResponse(element);
    }

    public String talkMessage(){
    	return toJsonObject().toString() ;
    }

	public String pushMessage() {
		return toString();
	}

    
    @Override
    public String toString() {
        return element.toString();
    }

    public JsonElement toJsonElement(){
        return element;
    }
    
    
    public <T> T transformer(Function<JsonElement, T> fn) {
    	return fn.apply(element) ;
    }

	public JsonObject toJsonObject() {
		return transformer(FnJsonObject);
	}

	public JsonArray toJsonArray() {
		return transformer(FnJsonArray);
	}

	public void debugString() {
		transformer(FnDebug) ;
	}

}
