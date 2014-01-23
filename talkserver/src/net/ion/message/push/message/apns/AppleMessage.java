package net.ion.message.push.message.apns;

import javapns.notification.PushNotificationPayload;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.message.push.sender.APNSSender;
import net.ion.message.push.sender.PushResponse;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

public class AppleMessage {

    private String token;
    private APNSSender apnsSender;

    private JsonObject attributes = JsonObject.create();

    public AppleMessage(APNSSender apnsSender, String token) {
        this.token = token;
        this.apnsSender = apnsSender;
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

    public PushResponse push() throws Exception {
        PushNotificationPayload payload = toPayload();

        return apnsSender.push(token, payload);
    }

    private PushNotificationPayload toPayload() {
        checkValidity();
        return createPayload();
    }

    private void checkValidity() {
        alertNotEmpty();
    }

    private void alertNotEmpty() {
        if(!attributes.has("alert") || StringUtils.isEmpty(attributes.asString("alert"))) {
            throw new IllegalStateException("message is null");
        }
    }

    private PushNotificationPayload createPayload() {
        PushNotificationPayload payload = PushNotificationPayload.complex();

        try {
            payload.addAlert(attributes.asString("alert"));
            payload.addBadge(attributes.asInt("badge"));
            payload.addSound(attributes.asString("sound"));

            if(payload.getMaximumPayloadSize() < payload.getPayloadSize()) {
                throw new IllegalStateException("Payload size should be under " + payload.getMaximumPayloadSize());
            }

            return payload;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

}
