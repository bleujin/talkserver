package net.ion.message.push.sender;

import org.apache.commons.lang.StringUtils;

import net.ion.message.push.sender.handler.PushResponseHandler;
import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.*;

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
		PushedNotifications results = null ;
		try {
			results = Push.payload(amsg.toPayload(), this.keyStore, this.password, this.isProduction, new BasicDevice(amsg.token()));
			PushedNotification firstResult = results.get(0) ;
			return firstResult.isSuccessful() ? handler.onAPNSSuccess(amsg, results) : handler.onAPNSFail(amsg, results) ;
		} catch (IllegalArgumentException ex) {
			return handler.onAPNSThrow(amsg, ex, results) ;
		} catch (Exception ex) {
			return handler.onAPNSThrow(amsg, ex, results) ;
		}
	}
}
