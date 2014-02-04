package net.ion.talk.script;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class ListBuilder extends AbstractBuilder {

    private List<BasicBuilder> list = ListUtil.newList();
    private BasicBuilder parent ;
    private BasicBuilder current ;
    protected ListBuilder(BasicBuilder parent) {
        this.parent = parent ;
    }

	public BasicBuilder parent() {
		return parent;
	}
    
    public ListBuilder next(){
		BasicBuilder created = new BasicBuilder(this) ;
		list.add(created) ;
		current = created ;
    	return this ;
    }
    
    public ListBuilder property(String name, Object value){
    	current.property(name, value) ;
    	return this ;
    }

	public ListBuilder property(ReadNode node, String values) {
		return (ListBuilder) super.property(node, values);
	}


	public AbstractBuilder property(Iterable<ReadNode> nodes, String values) {
		Iterator<ReadNode> iter = nodes.iterator() ;
		while(iter.hasNext()){
			ReadNode node = iter.next() ;
			property(node, values) ;
			if (iter.hasNext()) next() ;
		}
		return this;
	}

    
    @Override
    protected JsonElement makeJson() {
        JsonArray array = new JsonArray();
        for(BasicBuilder b : list){
        	array.add(b.makeJson());
        }

        return array;
    }

}
