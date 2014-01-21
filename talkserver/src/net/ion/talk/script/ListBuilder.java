package net.ion.talk.script;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.util.ListUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class ListBuilder extends AbstractBuilder {

    private List list = ListUtil.newList();

    protected ListBuilder() {
    }

    protected ListBuilder(AbstractBuilder parent) {
        this.parent = parent;
    }

    public ListBuilder add(Object value){
        list.add(value);
        return this;
    }

    public ListBuilder append(Object ... values){
        list.addAll(ListUtil.toList(values));
        return this;
    }

    @Override
    protected TalkResponse make() {

        JsonArray array = new JsonArray();

        Iterator<Map.Entry<String, Object>> iter = props.asMap().entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, Object> entry = iter.next();
            Object value = entry.getValue();
            if(value instanceof AbstractBuilder){
                array.add(((AbstractBuilder) value).make().toJsonElement());
            }

        }

        Iterator i = list.iterator();
        while(i.hasNext()){
            Object entry = i.next();
            array.adds(entry);
        }

        return TalkResponse.create(array);
    }
}
