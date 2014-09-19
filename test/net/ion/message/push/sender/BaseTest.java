package net.ion.message.push.sender;

import com.google.android.gcm.server.Result;

import javapns.notification.PushedNotifications;
import junit.framework.TestCase;
import net.ion.message.push.sender.handler.PushResponseHandler;


public class BaseTest extends TestCase {

	public static String INVALID_DEVICE_TOKEN = "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e7";
	public static String APPLE_DEVICE_TOKEN = "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e8";
	public static String GOOGLE_DEVICE_TOKEN = "APA91bFQsheILrtWpk6e-x-ZH6MN2BirntyZhEbmROoF5t0B0sJChWc7YvLbw0S8no6FBaNIo7vYyG7sKFrq3PhGC3w8mk36gQxKo53zaBLGm3lMDxjIJuZl9L10u2UaLxCZfOpvz2U81CeipH5GVMBlQ5wO-2KmeDZgwh6nlBnFQbhDIMgIYlY";
	public static String GCM_API_KEY = "AIzaSyBC_YDd2WfKy_K3T7r5PQo3M_dMfg5k5WA";
	public static String KEY_STORE_PATH = "./resource/keystore/toontalk.p12";
	public static String PASSWORD = "toontalk";

	public PushResponseHandler<Boolean> ConfirmResponseHandler = new PushResponseHandler<Boolean>() {

		@Override
		public Boolean onAPNSSuccess(AppleMessage amsg, PushedNotifications results) {
			return true;
		}

		@Override
		public Boolean onAPNSFail(AppleMessage amsg, PushedNotifications results) {
			return false;
		}

		@Override
		public Boolean onAPNSThrow(AppleMessage amsg, Exception ex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Boolean onGoogleSuccess(GoogleMessage gmsg, Result result) {
			return true;
		}

		@Override
		public Boolean onGoogleFail(GoogleMessage gmsg, Result result) {
			return false;
		}

		@Override
		public Boolean onGoogleThrow(GoogleMessage gmsg, Exception ex) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	public String createDummyMessage(int bytes) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bytes; i++) {
			builder.append("x");
		}
		return builder.toString();
	}
}
