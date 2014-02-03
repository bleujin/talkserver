package net.ion.talk.script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import net.ion.craken.node.ReadNode;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public abstract class AbstractBuilder {

    private Cache<String, Object> props = CacheBuilder.newBuilder().build() ;
    protected AbstractBuilder(){
    }
    
    public BasicBuilder inner(final String key) throws ExecutionException {
        return (BasicBuilder) props.get(key, new Callable<BasicBuilder>() {
            @Override
            public BasicBuilder call() throws Exception {
                BasicBuilder basic = new BasicBuilder(AbstractBuilder.this);
                props.put(key, basic);
                return basic;
            }
        });
    }
    
	public AbstractBuilder property(ReadNode node, String values) {
		String[] cols = StringUtil.split(values, ",") ;
		
		
		
		for(String col : cols){
			String propId = StringUtil.trim(col) ;
			if (propId.endsWith("[]")) {
				String propExpr = StringUtil.substringBeforeLast(propId, "[]");
				Set set = node.extendProperty(propExpr).asSet() ;
				property(propExpr, set.toArray()) ;
			} else {
				Object propValue = node.extendProperty(propId).value();
				property(propId, ObjectUtil.coalesce(propValue, ObjectUtil.NULL)) ;
			}
		}
		
		return this;
	}

    public Cache<String, Object> props(){
    	return props ;
    }

    public AbstractBuilder root() {
        AbstractBuilder parent = this.parent();
        if (parent == null) return this ;
        while(!parent.isRoot()){
            parent = parent.parent();
        }
        return parent;
    }

    public abstract AbstractBuilder parent() ;

    public boolean isRoot() {
        return parent() == null ? true : false;
    }

    public abstract AbstractBuilder property(String key, Object value) ;
    
    protected abstract JsonElement makeJson();

    public TalkResponse build(){
        return TalkResponse.create(root().makeJson());
    }

}
