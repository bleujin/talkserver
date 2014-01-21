package net.ion.talk.script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public abstract class AbstractBuilder {


    protected Cache<String, Object> props = CacheBuilder.newBuilder().build() ;
    protected AbstractBuilder parent;

    public ListBuilder inlist(final String key) throws ExecutionException {


        return (ListBuilder) props.get(key, new Callable<ListBuilder>() {
            @Override
            public ListBuilder call() throws Exception {
                ListBuilder lb = new ListBuilder(AbstractBuilder.this);
                props.put(key, lb);
                return lb;
            }
        });
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

    public AbstractBuilder root() {
        AbstractBuilder parent = this.parent();
        while(!parent.isRoot()){
            parent = parent.parent();
        }
        return parent;
    }

    public AbstractBuilder parent() {
        return parent == null ? this : parent;
    }

    public ListBuilder asList(){
        return (ListBuilder) this;
    }

    public BasicBuilder asObject(){
        return (BasicBuilder) this;
    }

    public boolean isRoot() {
        return parent == null ? true : false;
    }


    protected abstract TalkResponse make();

    public TalkResponse build(){
        return root().make();
    }

}
