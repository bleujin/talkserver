package net.ion.message.push.sender;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ion.message.push.sender.handler.PushResponseHandler;

public class PushMessage {

    private Pusher sender;
    private String receiver;
    private String message;

    public PushMessage(Pusher sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public boolean send(String message) throws InterruptedException, ExecutionException {
        return send(message, PushResponseHandler.DEFAULT);
    }

    public <T> T send(String message, PushResponseHandler<T> handler) throws InterruptedException, ExecutionException {
    	return sendAsync(message, handler).get();
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public <T> Future<T> sendAsync(String message, PushResponseHandler<T> handler) {
        this.message = message;
        return sender.send(this, handler);
    }

    public Future<Boolean> sendAsync(String message) {
        return sendAsync(message, PushResponseHandler.DEFAULT);
    }

//    public <T> Future<T> sendSchedule(String message, int sendAfter, TimeUnit timeUnit, ResponseHandler<T> handler) throws InterruptedException {
//        Thread.sleep(TimeUnit.MILLISECONDS.convert(sendAfter, timeUnit));
//
//        return sendAsync(message, handler);
//    }
}
