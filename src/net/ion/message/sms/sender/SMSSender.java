package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.concurrent.Future;

import net.ion.message.sms.response.MessagingResponse;
import net.ion.message.sms.response.ResponseHandler;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

public class SMSSender {

	private NewClient client;
	private SMSConfig config;

	public SMSSender(NewClient client, SMSConfig config) {
		this.client = client;
		this.config = config;
	}

	public PhoneMessage toPhoneNo(String receiverPhone) {
		return PhoneMessage.create(this, receiverPhone);
	}

	public SMSConfig config() {
		return config;
	}

	public <T> Future<T> send(PhoneMessage message, final ResponseHandler<T> handler) throws IOException {
		return this.client.executeRequest(message.toRequest(), new AsyncCompletionHandler<T>() {
			@Override
			public T onCompleted(Response response) throws Exception {
				MessagingResponse resp = MessagingResponse.from(response);
				return (response.getStatus().getCode() == 200) ? handler.onSuccess(resp) : handler.onFail(resp);
			}

			public void onThrowable(Throwable ex) {
				handler.onThrow(ex);
			}
		});
	}

}
