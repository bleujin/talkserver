package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
import net.ion.message.push.sender.Sender;
import net.ion.talk.responsebuilder.TalkResponse;


/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 5:04
 * To change this template use File | Settings | File Templates.
 */
public class DisconnectedAccount extends Account {

    private final Sender sender;

    DisconnectedAccount(String userId, ReadNode user, Sender sender) {
        super(userId, Type.DisconnectedUser);
        this.sender = sender;
    }

    @Override
    public Object onMessage(TalkResponse response) {
        return sender.sendTo(accountId()).sendAsync(response.pushMessage());
    }

    Sender sender(){
        return sender;
    }

}
