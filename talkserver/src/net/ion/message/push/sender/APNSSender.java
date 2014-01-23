package net.ion.message.push.sender;

import javapns.Push;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.*;
import net.ion.message.push.message.apns.AppleMessage;

public class APNSSender {

    private String keyStore;
    private String password;
    private boolean isProduction;

    private APNSSender(String keyStore, String password, boolean isProduction) {
        this.keyStore = keyStore;
        this.password = password;
        this.isProduction = isProduction;
    }

    public static APNSSender create(String keyStore, String password, boolean isProduction) {
        return new APNSSender(keyStore, password, isProduction);
    }

    public PushResponse push(String token, PushNotificationPayload payload) throws Exception {
        PushedNotifications results = Push.payload(payload, this.keyStore, this.password, this.isProduction, new BasicDevice(token));
        return PushResponse.from(results.get(0));
    }

    public AppleMessage newMessage(String token) {
        return new AppleMessage(this, token);
    }
}
