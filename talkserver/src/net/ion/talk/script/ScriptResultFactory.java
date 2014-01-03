package net.ion.talk.script;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.ion.craken.node.ReadNode;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.SetUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Author: Ryunhee Han
 * Date: 2013. 12. 31.
 */
public class ScriptResultFactory {


    private String scriptName;

    private Multimap<String, Object> objMap = ArrayListMultimap.create();
    private Set<String> ignoreSet;
    private ResultRule rule;

    private ScriptResultFactory(String scriptName) {
        this.scriptName = scriptName;
    }

    public static ScriptResultFactory create(String scriptName) {
        return new ScriptResultFactory(scriptName);
    }

    public String make(){
        return JsonObject.fromObject(objMap.asMap()).toString();
    }

    public ScriptResultFactory setRule(ResultRule rule){
        this.rule = rule;
        return this;
    }

    public ScriptResultFactory addNodes(String key, ReadNode... nodes){
        for(ReadNode node : nodes){
            Map entryMap = MapUtil.newMap();
            rule.apply(node, entryMap, ignoreSet);
            objMap.put(key, entryMap);
        }
        return this;
    }

    public ScriptResultFactory setIgnoreProperty(String ... properties){
        ignoreSet = SetUtil.newSet();
        Collections.addAll(ignoreSet, properties);
        return this;
    }
}