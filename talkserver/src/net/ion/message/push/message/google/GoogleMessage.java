package net.ion.message.push.message.google;

import com.google.android.gcm.server.Message;
import net.ion.message.push.sender.GCMSender;
import net.ion.message.push.sender.PushResponse;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GoogleMessage {

    private int timeToLive = -1;
    private String collapseKey;
    private boolean delayWhileIdle = false;
    private String message;

    private GCMSender sender;
    private String receiver;

    private GoogleMessage(String receiver, GCMSender sender) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public static GoogleMessage create(String receiver, GCMSender sender) {
        return new GoogleMessage(receiver, sender);
    }

    public GoogleMessage message(String message) {
        this.message = message;
        return this;
    }

    public GoogleMessage delayWhenIdle(boolean isWait) {
        this.delayWhileIdle = isWait;
        return this;
    }

    public GoogleMessage timeToLive(int lifetime) {
        this.timeToLive = lifetime;
        return this;
    }

    public GoogleMessage collapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
        return this;
    }

    public PushResponse push() throws IOException {
        checkValidity();
        return this.sender.send(this);
    }

    private void checkValidity() {
        messageNotEmpty();
        tooLargeMessage();
    }

    private final int MAX_MESSAGE_BYTES = 4096;

    private void tooLargeMessage() {
        try {

            if(this.message.getBytes("UTF-8").length > MAX_MESSAGE_BYTES) {
                throw new IllegalStateException("Message is larger than 4096 bytes");
            }

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void messageNotEmpty() {
        if (StringUtils.isEmpty(this.message)) {
            throw new IllegalStateException("message is null");
        }
    }

    public String getReceiver() {
        return receiver;
    }

    public Message toPayload() {

        Message.Builder builder = new Message.Builder();

        if(this.collapseKey != null) {
            builder.collapseKey(this.collapseKey);
        }

        if(this.timeToLive != -1) {
            builder.timeToLive(this.timeToLive);
        }

        return builder.delayWhileIdle(this.delayWhileIdle).addData("message", this.message).build();
    }
}
