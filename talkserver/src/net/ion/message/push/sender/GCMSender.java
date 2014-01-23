package net.ion.message.push.sender;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import net.ion.message.push.message.google.GoogleMessage;

import java.io.IOException;

public class GCMSender {

    private com.google.android.gcm.server.Sender sender;

    private GCMSender(String apiKey) {
        this.sender = new com.google.android.gcm.server.Sender(apiKey);
    }

    public static GCMSender create(String apiKey) {
        return new GCMSender(apiKey);
    }

    public GoogleMessage newMessage(String token) {
        return GoogleMessage.create(token, this);
    }

    public PushResponse send(GoogleMessage message) throws IOException {
        Message payload = message.toPayload();

        Result result = sender.sendNoRetry(payload, message.getReceiver());

        return PushResponse.from(result);
    }
}
