package net.ion.message.push.sender;

import net.ion.message.push.sender.handler.DefaultResponseHandler;
import net.ion.message.push.sender.handler.ResponseHandler;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PushMessage {

    private Sender sender;
    private String[] receivers;
    private String message;

    public PushMessage(Sender sender, String[] receivers) {
        this.sender = sender;
        this.receivers = receivers;
    }

    public List<PushResponse> send(String message) {
        return send(message, DefaultResponseHandler.create(this));
    }

    public <T> T send(String message, ResponseHandler<T> handler) {
        try {
            return sendAsync(message, handler).get();
        } catch (InterruptedException e) {
            throw new IllegalThreadStateException(e.getMessage());
        } catch (ExecutionException e) {
            throw new IllegalThreadStateException(e.getMessage());
        }
    }

    public String[] getReceivers() {
        return receivers;
    }

    public String getMessage() {
        return message;
    }

    public <T> Future<T> sendAsync(String message, ResponseHandler<T> handler) {
        this.message = message;
        return sender.send(this, handler);
    }

    public Future<List<PushResponse>> sendAsync(String message) {
        return sendAsync(message, DefaultResponseHandler.create(this));
    }

    public <T> Future<T> sendSchedule(String message, int sendAfter, TimeUnit timeUnit, ResponseHandler<T> handler) throws InterruptedException {
        Thread.sleep(TimeUnit.MILLISECONDS.convert(sendAfter, timeUnit));

        return sendAsync(message, handler);
    }
}
