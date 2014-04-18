package net.ion.talk.fake;

import net.ion.message.push.sender.PushMessage;
import net.ion.message.push.sender.Sender;
import net.ion.message.push.sender.handler.ResponseHandler;

import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 16.
 * Time: 오후 5:11
 * To change this template use File | Settings | File Templates.
 */
public class FakeSender extends Sender {
    private String message;

    public FakeSender(){
        super(null, null, null);
    }

    @Override
    public <T> Future<T> send(final PushMessage pushMessage, ResponseHandler<T> handler) {

        message = pushMessage.getMessage();
        return null;

    }

    public String getMessage() throws InterruptedException {
        return message;
    }
}