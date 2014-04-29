package net.ion.message.push.sender;

import javapns.Push;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import net.ion.message.push.sender.handler.PushResponseHandler;

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

    public AppleMessage sendTo(String receiver, String token) {
        return new AppleMessage(this, receiver, token);
    }

	<T> T push(AppleMessage amsg, PushResponseHandler<T> handler) {
		try {
			PushedNotifications results = Push.payload(amsg.toPayload(), this.keyStore, this.password, this.isProduction, new BasicDevice(amsg.token()));
			PushedNotification firstResult = results.get(0) ;
			return firstResult.isSuccessful() ? handler.onAPNSSuccess(amsg, results) : handler.onAPNSFail(amsg, results) ;
		} catch (IllegalArgumentException ex) {
			return handler.onAPNSThrow(amsg, ex) ;
		} catch (Exception ex) {
			return handler.onAPNSThrow(amsg, ex) ;
		}
	}
}
