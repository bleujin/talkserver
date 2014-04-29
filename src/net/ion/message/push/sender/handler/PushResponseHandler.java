package net.ion.message.push.sender.handler;

import com.google.android.gcm.server.Result;

import javapns.notification.PushedNotifications;
import net.ion.message.push.sender.AppleMessage;
import net.ion.message.push.sender.GoogleMessage;

public interface PushResponseHandler<T> {

	public T onAPNSSuccess(AppleMessage amsg, PushedNotifications results);

	public T onAPNSFail(AppleMessage amsg, PushedNotifications results);

	public T onAPNSThrow(AppleMessage amsg, Exception ex, PushedNotifications results);

	public T onGoogleSuccess(GoogleMessage gmsg, Result result);

	public T onGoogleFail(GoogleMessage gmsg, Result result);

	public T onGoogleThrow(GoogleMessage gmsg, Exception ex, Result result);

	
	public final static PushResponseHandler<Boolean> DEFAULT = new PushResponseHandler<Boolean>() {
		@Override
		public Boolean onAPNSSuccess(AppleMessage amsg, PushedNotifications results) {
			return Boolean.TRUE;
		}

		@Override
		public Boolean onAPNSFail(AppleMessage amsg, PushedNotifications results) {
			return Boolean.FALSE;
		}

		@Override
		public Boolean onAPNSThrow(AppleMessage amsg, Exception ex, PushedNotifications results) {
			ex.printStackTrace();
			return Boolean.FALSE;
		}

		@Override
		public Boolean onGoogleSuccess(GoogleMessage gmsg, Result result) {
			return Boolean.TRUE;
		}

		@Override
		public Boolean onGoogleFail(GoogleMessage gmsg, Result result) {
			return Boolean.FALSE;
		}

		@Override
		public Boolean onGoogleThrow(GoogleMessage gmsg, Exception ex, Result result) {
			ex.printStackTrace();
			return Boolean.FALSE;
		}
	};
}
