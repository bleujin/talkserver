package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyValue;
import net.ion.message.push.sender.Sender;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 4:15
 * To change this template use File | Settings | File Templates.
 */
public class AccountManager{


    private final Sender sender;
    private final TalkEngine tengine;
    private ReadSession session;
    private NewClient newClient;

    public AccountManager(TalkEngine tengine, Sender sender) throws IOException {
        this.tengine = tengine;
        this.sender = sender;
        init();
    }

    protected void init() throws IOException {
        session = tengine.readSession();
        this.newClient = tengine.context().getAttributeObject(NewClient.class.getCanonicalName(), NewClient.class);
    }

    public static AccountManager create(TalkEngine tengine, Sender sender) throws IOException {
        return new AccountManager(tengine, sender);
    }

    public Account newAccount(String userId) {

        UserConnection uconn = tengine.findConnection(userId);

        if(uconn!=null){
            return new ConnectedUserAccount(userId, session, uconn);
        }else if(session.exists("/users/"+userId)){
            ReadNode user = session.pathBy("/users/" + userId);
            return user.property("requestURL") == PropertyValue.NotFound ? new DisconnectedAccount(userId, session, sender) : new Bot(userId, session, newClient);
        }

        return Account.NotFoundUser;
    }

}
