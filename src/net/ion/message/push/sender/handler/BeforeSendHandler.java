package net.ion.message.push.sender.handler;

import net.ion.message.push.sender.PushMessage;

public interface BeforeSendHandler {

    public void handle(PushMessage message);

}
