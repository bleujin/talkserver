package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
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

    ConnectedUserAccount(String userId, ReadNode readNode, UserConnection uconn) {
        super(userId, Type.ConnectedUser);
        this.uconn = uconn;
    }

    @Override
    public Object onMessage(TalkResponse response) {
        uconn.sendMessage(response.talkMessage());
        return null;
    }

    public UserConnection userConnection(){
        return uconn;
    }


}
