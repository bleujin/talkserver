package net.ion.talk.account;

import java.util.Map;

import net.ion.craken.node.crud.TreeNodeKey;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;

import org.infinispan.atomic.AtomicHashMap;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

public class EventMap {

	public final static EventMap EMPTY = new EventMap(new AtomicHashMap<PropertyId, PropertyValue>()) ;
	
	private Map<PropertyId, PropertyValue> vmap;
	private EventMap(Map<PropertyId, PropertyValue> vmap) {
		this.vmap = vmap ;
	}

	public static EventMap create(Map<PropertyId, PropertyValue> value) {
		return new EventMap(value) ;
	}

	
	public static EventMap create(CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {
		return new EventMap(event.getValue()) ;
	}

	
	public PropertyValue property(String propId){
		PropertyId pid = PropertyId.normal(propId) ;
		return vmap.containsKey(pid) ? vmap.get(pid) : PropertyValue.NotFound ;
	}

	public PropertyValue refer(String propId){
		PropertyId pid = PropertyId.refer(propId) ;
		return vmap.containsKey(pid) ? vmap.get(pid) : PropertyValue.NotFound ;
	}

	public PropertyValue idString(String propId) {
		PropertyId pid = PropertyId.fromIdString(propId) ;
		return vmap.containsKey(pid) ? vmap.get(pid) : PropertyValue.NotFound ;
	}

	

	
}
