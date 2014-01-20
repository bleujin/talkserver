package net.ion.talk.script;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class JsonResponse {

    private JsonElement element;

    private JsonResponse(JsonElement element) {
        this.element = element;
    }

    public static JsonResponse create(JsonElement element){
        return new JsonResponse(element);
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
