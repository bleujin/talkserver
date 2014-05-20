package net.ion.talk.account;

import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
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
        super(userId, Type.CONNECTED_USER);
        this.uconn = uconn;
        this.rsession = rsession;
    }

    @Override
    public void onMessage(final String notifyId) {
        uconn.sendMessage(new JsonObject().put("notifyId", notifyId).toString());
    }

    public UserConnection userConnection(){
        return uconn;
    }


}
