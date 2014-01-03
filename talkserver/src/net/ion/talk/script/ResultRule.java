package net.ion.talk.script;

import net.ion.craken.node.ReadNode;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;

import java.util.Map;
import java.util.Set;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 2.
 */
public abstract class ResultRule {

    public static ResultRule PROPERTY_ALL = new ResultRule() {
        @Override
        public void apply(ReadNode node, Map map, Set<String> ignoreSet) {
            for(Map.Entry entry : node.toMap().entrySet()){
                map.put(((PropertyId) entry.getKey()).getString(), ((PropertyValue) entry.getValue()).stringValue());
            }
        }
    };

    public static ResultRule IGNORE_PROPERTY = new ResultRule() {
        @Override
        public void apply(ReadNode node, Map map, Set<String> ignoreSet) {
            for(Map.Entry entry : node.toMap().entrySet()){
                String property = ((PropertyId) entry.getKey()).getString();
                if(!ignoreSet.contains(property)){
                    map.put(property, ((PropertyValue) entry.getValue()).stringValue());
                }
            }
        }
    };




    abstract void apply(ReadNode node, Map map, Set<String> ignoreSet);
}
