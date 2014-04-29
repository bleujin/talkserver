package net.ion.message.push.sender;

import javapns.notification.PushNotificationPayload;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.message.push.sender.handler.PushResponseHandler;

import org.apache.lucene.analysis.kr.utils.StringUtil;
import org.json.JSONException;

public class AppleMessage {

    private String token;
    private APNSSender apnsSender;

    private JsonObject attributes = JsonObject.create();
	private String receiver;

    public AppleMessage(APNSSender apnsSender, String receiver, String token) {
    	this.apnsSender = apnsSender;
    	this.receiver = receiver ; 
        this.token = token;
    }


    public AppleMessage message(String message) throws JSONException {
        this.attributes.put("alert", message);
        return this;
    }

    public AppleMessage badge(int badge) throws JSONException {
        this.attributes.put("badge", badge);
        return this;
    }

    public AppleMessage sound(String sound) throws JSONException {
        this.attributes.put("sound", sound);
        return this;
    }

    public <T> T push(PushResponseHandler<T> handler) {
        return apnsSender.push(this, handler);
    }

    PushNotificationPayload toPayload() throws Exception {
    	if (isBlankMsg()) throw new IllegalArgumentException("Message is null") ;
        PushNotificationPayload payload = PushNotificationPayload.complex();

        payload.addAlert(attributes.asString("alert"));
        payload.addBadge(attributes.asInt("badge"));
        payload.addSound(attributes.asString("sound"));

        if(payload.getMaximumPayloadSize() < payload.getPayloadSize()) {
            throw new IllegalArgumentException("Payload size should be under " + payload.getMaximumPayloadSize());
        }

        return payload;
    }
    
    public APNSSender sender(){
    	return apnsSender ;
    }

    public String token(){
    	return this.token ;
    }
    
    public String receiver() {
    	return this.receiver ;
    }

	boolean isBlankMsg() {
		return !attributes.has("alert") || StringUtil.isBlank(attributes.asString("alert"));
	}

}
