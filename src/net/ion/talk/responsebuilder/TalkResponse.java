package net.ion.talk.responsebuilder;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.core.representation.JsonObjectRepresentation;

import com.google.common.base.Function;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class TalkResponse {

	public final static Function<JsonElement, Representation> ToJsonRepresentation = new Function<JsonElement, Representation>() {
		@Override
		public Representation apply(JsonElement jele) {
			return new JsonObjectRepresentation(jele);
		}
	};
	public final static Function<JsonElement, Representation> ToStringRepresentation = new Function<JsonElement, Representation>() {
		@Override
		public Representation apply(JsonElement jele) {
			return new StringRepresentation(ObjectUtil.toString(jele));
		}
	};
    
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
