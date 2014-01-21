package net.ion.talk.script;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class TalkResponse {

    private JsonElement element;

    private TalkResponse(JsonElement element) {
        this.element = element;
    }

    public static TalkResponse create(JsonElement element){
        return new TalkResponse(element);
    }

    @Override
    public String toString() {
        return element.toString();
    }

    public JsonObject toJsonObject() {
        return (JsonObject) element;
    }

    public JsonArray toJsonArray(){
        return (JsonArray) element;
    }

    public JsonElement toJsonElement(){
        return element;
    }
}
