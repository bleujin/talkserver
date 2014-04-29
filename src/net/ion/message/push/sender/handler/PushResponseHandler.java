package net.ion.message.push.sender.handler;

import javapns.notification.PushedNotifications;
import net.ion.message.push.sender.AppleMessage;
import net.ion.message.push.sender.GoogleMessage;

import com.google.android.gcm.server.Result;

public interface PushResponseHandler<T> {

	public T onAPNSSuccess(AppleMessage amsg, PushedNotifications results);

	public T onAPNSFail(AppleMessage amsg, PushedNotifications results);

	public T onAPNSThrow(AppleMessage amsg, Exception ex);

	public T onGoogleSuccess(GoogleMessage gmsg, Result result);

	public T onGoogleFail(GoogleMessage gmsg, Result result);

	public T onGoogleThrow(GoogleMessage gmsg, Exception ex);

	
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
		public Boolean onAPNSThrow(AppleMessage amsg, Exception ex) {
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
		public Boolean onGoogleThrow(GoogleMessage gmsg, Exception ex) {
			ex.printStackTrace();
			return Boolean.FALSE;
		}
	};
}
