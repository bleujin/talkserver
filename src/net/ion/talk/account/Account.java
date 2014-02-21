package net.ion.talk.account;

import net.ion.talk.responsebuilder.TalkResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

    public enum Type{
        ConnectedUser, DisconnectedUser, NotFoundUser, Bot
    }

    public abstract Object onMessage(TalkResponse response);

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

    public static Account NotFoundUser = new Account("notFound", Type.NotFoundUser) {
        @Override
        public Object onMessage(TalkResponse s) {
            return null;
        }

    };
}
