package net.ion.talk.fake;

import net.ion.craken.node.crud.TreeNodeKey;
import net.ion.craken.tree.Fqn;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.talk.bean.Const;
import org.infinispan.Cache;
import org.infinispan.atomic.AtomicHashMap;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.transaction.xa.GlobalTransaction;


public class FakeEvent implements CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> {
    private Type type;
    private boolean pre;
    private TreeNodeKey treeNodeKey;
    private AtomicMap<PropertyId, PropertyValue> value;

    @Override
    public AtomicMap<PropertyId, PropertyValue> getValue() {
        return value;
    }

    public void setValue(AtomicMap<PropertyId, PropertyValue> value){
        this.value = value;
    }

    @Override
    public TreeNodeKey getKey() {
        return treeNodeKey;
    }

    public void setKey(TreeNodeKey treeNodeKey){
        this.treeNodeKey = treeNodeKey;
    }

    @Override
    public GlobalTransaction getGlobalTransaction() {
        return null;
    }

    @Override
    public boolean isOriginLocal() {
        return false;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type){
        this.type = type;
    }

    @Override
    public boolean isPre() {
        return pre;
    }

    public void setPre(boolean pre){
        this.pre = pre;
    }

    @Override
    public Cache<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> getCache() {
        return null;
    }
}