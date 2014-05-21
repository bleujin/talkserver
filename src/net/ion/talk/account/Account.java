package net.ion.talk.account;

import net.ion.talk.responsebuilder.TalkResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 4:15
 * To change this template use File | Settings | File Templates.
 */
public abstract class Account {

    private final Type type;
    private final String accountId;

	public enum Type {
		CONNECTED_USER, DISCONNECTED_USER, BOT, NOT_REGISTERED, PROXY ;
	}

    public abstract void onMessage(String notifyId) ;

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
