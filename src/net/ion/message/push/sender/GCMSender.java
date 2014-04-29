package net.ion.message.push.sender;

import java.io.IOException;

import net.ion.message.push.sender.handler.PushResponseHandler;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;

public class GCMSender {

    private com.google.android.gcm.server.Sender sender;

    private GCMSender(String apiKey) {
        this.sender = new com.google.android.gcm.server.Sender(apiKey);
    }

    public static GCMSender create(String apiKey) {
        return new GCMSender(apiKey);
    }

    public GoogleMessage sendTo(String receiver, String token) {
        return GoogleMessage.create(this, receiver, token);
    }

    <T> T send(GoogleMessage gmsg, PushResponseHandler<T> handler) {
    	Result result = null ;
		try {
			Message payload = gmsg.toPayload();
			result = sender.sendNoRetry(payload, gmsg.token());
			return  result.getMessageId() != null ? handler.onGoogleSuccess(gmsg, result) : handler.onGoogleFail(gmsg, result) ;
		} catch (IOException ex) {
			return handler.onGoogleThrow(gmsg, ex, result) ;
		}

    }
}
