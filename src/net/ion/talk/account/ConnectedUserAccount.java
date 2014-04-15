package net.ion.talk.account;

import com.google.common.base.Predicate;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.UserConnection;
import net.ion.talk.responsebuilder.TalkResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 4:38
 * To change this template use File | Settings | File Templates.
 */
public class ConnectedUserAccount extends Account {

    private final UserConnection uconn;
    private final ReadSession rsession;

    ConnectedUserAccount(String userId, ReadSession rsession, UserConnection uconn) {
        super(userId, Type.ConnectedUser);
        this.uconn = uconn;
        this.rsession = rsession;
    }

    @Override
    public Object onMessage(final String notifyId, TalkResponse response) throws Exception {
        uconn.sendMessage(response.talkMessage());

        return null;
    }

    public UserConnection userConnection(){
        return uconn;
    }


}
