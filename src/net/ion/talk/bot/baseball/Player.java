package net.ion.talk.bot.baseball;

import java.util.Map;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;

public class Player {

    Map<String, String> attrs = MapUtil.newOrdereddMap();

    public JsonObject toJson() {
        return JsonObject.fromObject(attrs);
    }

    public void setAttr(String item, String value) {
        attrs.put(item, value);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
