package net.ion.message.push.sender;

import net.ion.message.push.sender.handler.PushResponseHandler;

import org.apache.commons.lang.StringUtils;

import com.google.android.gcm.server.Message;

public class GoogleMessage {

    private int timeToLive = -1;
    private String collapseKey;
    private boolean delayWhileIdle = false;
    private String message;

    private final int MAX_MESSAGE_BYTES = 1024;

    private GCMSender sender;
    private String receiver;
    private String token;

    private GoogleMessage(GCMSender sender, String receiver, String token) {
        this.sender = sender;
        this.receiver = receiver ;
        this.token = token;
    }

    public static GoogleMessage create(GCMSender sender, String receiver, String token) {
        return new GoogleMessage(sender, receiver, token);
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

    public <T> T push(PushResponseHandler<T> handler) {
        if (StringUtils.isBlank(this.message)) return handler.onGoogleThrow(this, new IllegalArgumentException("Message is null")) ;
        if (this.message.length() > MAX_MESSAGE_BYTES) return handler.onGoogleThrow(this, new IllegalArgumentException("Message is larger than 4096 bytes")) ;

        return this.sender.send(this, handler);
    }


    public GCMSender sender(){
    	return sender ;
    }
    
    public String token() {
        return token;
    }
    
    public String receiver(){
    	return receiver ;
    }

    
    Message toPayload() {

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
