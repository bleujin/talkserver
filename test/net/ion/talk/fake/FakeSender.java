package net.ion.talk.fake;

import java.util.concurrent.Future;

import org.infinispan.util.concurrent.WithinThreadExecutor;

import net.ion.message.push.sender.PushMessage;
import net.ion.message.push.sender.Pusher;
import net.ion.message.push.sender.PusherConfig;
import net.ion.message.push.sender.handler.PushResponseHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 16.
 * Time: 오후 5:11
 * To change this template use File | Settings | File Templates.
 */
public class FakeSender extends Pusher {
    private String message;

    public FakeSender(){
        super(null, PusherConfig.createTest());
    }

    @Override
    public <T> Future<T> send(final PushMessage pushMessage, PushResponseHandler<T> handler) {

        message = pushMessage.getMessage();
        return null;

    }

    public String getMessage() throws InterruptedException {
        return message;
    }
}