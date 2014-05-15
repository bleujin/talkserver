package net.ion.talk.account;

import java.io.IOException;
import java.util.logging.Logger;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.logging.LogBroker;
import net.ion.message.push.sender.Pusher;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;
import net.ion.talk.account.Account.Type;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.script.BotScript;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 4:15
 * To change this template use File | Settings | File Templates.
 */
public class AccountManager{


    private final Pusher pusher;
    private BotScript bs;
    private final TalkEngine tengine;
    private ReadSession session;
    private Logger log = LogBroker.getLogger(AccountManager.class) ;

    private Account NotRegisteredUser = new Account("notFound", Type.NotFoundUser) {
        @Override
        public void onMessage(String notifyId, TalkResponse s) {
        	log.warning("not registed user");
        }
    };

    
    protected AccountManager(BotScript bs, TalkEngine tengine, Pusher pusher) throws IOException {
    	this.bs = bs ;
        this.tengine = tengine;
        this.pusher = pusher;
        init();
    }

    protected void init() throws IOException {
        session = tengine.readSession();
    }

    public static AccountManager create(BotScript bs, TalkEngine tengine, Pusher sender) throws IOException {
        return new AccountManager(bs, tengine, sender);
    }

    public Account newAccount(String userId) {

        UserConnection uconn = tengine.findConnection(userId);

        if(uconn != UserConnection.NOTFOUND){
            return new ConnectedUserAccount(userId, session, uconn);
        } else if(uconn == UserConnection.NOTFOUND && session.exists("/bots/"+userId)){
            return new BotAccount(bs, session, userId);
        } else if (uconn == UserConnection.NOTFOUND && session.exists("/users/"+userId)){
            return new DisconnectedAccount(userId, session, pusher) ;
        }

        return NotRegisteredUser;
    }

}
