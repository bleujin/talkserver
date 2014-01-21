package net.ion.talk.script;

import net.ion.framework.parse.gson.JsonObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class BasicBuilder extends AbstractBuilder {

    protected BasicBuilder() {
    }

    public BasicBuilder(AbstractBuilder parent) {
        this.parent = parent;
    }

    public BasicBuilder property(String key, Object value) {
        props.put(key, value);
        return this;
    }

    @Override
    protected TalkResponse make() {

        JsonObject obj = JsonObject.create();

        Iterator<Map.Entry<String, Object>> iter = props.asMap().entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof AbstractBuilder){
                obj.put(key, ((AbstractBuilder) value).make().toJsonElement());
            }else{
                obj.put(key, value);
            }
        }

        return TalkResponse.create(obj);
    }
}
