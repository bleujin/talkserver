package net.ion.talk.account;

import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;

import org.infinispan.atomic.AtomicMap;


public abstract class Account {

    private final Type type;
    private final String accountId;

	public enum Type {
		CONNECTED_USER, DISCONNECTED_USER, BOT, NOT_REGISTERED, PROXY ;
	}

    public abstract void onMessage(String notifyId, AtomicMap<PropertyId, PropertyValue> pmap) ;

    public Type type(){
        return type;
    }

    public String accountId(){
        return accountId;
    }

    protected Account(String id, Type type) {
        this.accountId = id;
        this.type = type;
    }

}
