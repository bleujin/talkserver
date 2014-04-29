package net.ion.message.push.sender.handler;

import com.google.android.gcm.server.Result;

import javapns.notification.PushedNotifications;
import net.ion.message.push.sender.AppleMessage;
import net.ion.message.push.sender.GoogleMessage;

public class BlankPushResponseHandler implements PushResponseHandler<Void> {

	@Override
	public Void onAPNSSuccess(AppleMessage amsg, PushedNotifications results) {
		return null;
	}

	@Override
	public Void onAPNSFail(AppleMessage amsg, PushedNotifications results) {
		return null;
	}

	@Override
	public Void onAPNSThrow(AppleMessage amsg, Exception ex, PushedNotifications results) {
		return null;
	}

	@Override
	public Void onGoogleSuccess(GoogleMessage gmsg, Result result) {
		return null;
	}

	@Override
	public Void onGoogleFail(GoogleMessage gmsg, Result result) {
		return null;
	}

	@Override
	public Void onGoogleThrow(GoogleMessage gmsg, Exception ex, Result result) {
		return null;
	}

}
