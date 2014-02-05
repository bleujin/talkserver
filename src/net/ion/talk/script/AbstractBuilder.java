package net.ion.talk.script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import net.ion.craken.expression.ExpressionParser;
import net.ion.craken.expression.Projection;
import net.ion.craken.expression.SetComparable;
import net.ion.craken.expression.TerminalParser;
import net.ion.craken.expression.ValueObject;
import net.ion.craken.node.ReadNode;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.rosetta.Parser;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public abstract class AbstractBuilder {

    private Cache<String, Object> props = CacheBuilder.newBuilder().build() ;
    private Parser<MyProjection> parser = ExpressionParser.projections2(MyProjection.class) ;
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
		MyProjection sp = TerminalParser.parse(parser, values);
		Map<String, Object> map = sp.map(node) ;

		for(Entry<String, Object> entry : map.entrySet()){
			Object value = entry.getValue();
			if (value instanceof SetComparable) {
				value = ((SetComparable)value).asSet() ;
			}
			property(entry.getKey(), ObjectUtil.coalesce(value, ObjectUtil.NULL)) ;
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


class MyProjection extends ValueObject {
	private List<Projection> projections;

	public MyProjection (List<Projection> projections) {
		this.projections = projections;
	}
	
	public Map<String, Object> map(ReadNode node){
		Map<String, Object> map = MapUtil.newMap() ;
		for (Projection p : projections) {
			map.put(p.label(), p.value(node)) ;
		}
		return map ;
	}
	
	
	public List<Map<String, Object>> mapList(Iterable<ReadNode> nodes){
		List<Map<String, Object>> result = ListUtil.newList() ;
		for (ReadNode node : nodes) {
			result.add(map(node)) ;
		}
		return result ;
	} 
	
	
}
