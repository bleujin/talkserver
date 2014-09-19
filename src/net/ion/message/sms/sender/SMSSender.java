package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.concurrent.Future;

import net.ion.message.sms.response.ResponseHandler;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

public class SMSSender {

	private NewClient client;

	SMSSender(NewClient client) {
		this.client = client;
	}
	
	public static SMSSender create(NewClient nc){
		return new SMSSender(nc) ;
	}

	public PhoneMessage toPhoneNo(String receiverPhone) {
		boolean isDomestic = !receiverPhone.startsWith("+");
		return PhoneMessage.create(this, receiverPhone, isDomestic);
	}
	
	public PhoneMessage toPhoneNo(String exchangeNo, String prefixNo, String postfixNo) {
		return PhoneMessage.create(this, exchangeNo + prefixNo + postfixNo, true);
	}
	
	public PhoneMessage toPhoneNo(String nationalCode, String exchangeNo, String prefixNo, String postfixNo) {
		String receiverPhone = String.format("+%s-%s%s%s", nationalCode, exchangeNo, prefixNo, postfixNo);
		return PhoneMessage.create(this, receiverPhone, "82".equals(nationalCode));
	}

	public <T> Future<T> send(final PhoneMessage message, final ResponseHandler<T> handler) throws IOException {
		return this.client.executeRequest(message.toRequest(), new AsyncCompletionHandler<T>() {
			@Override
			public T onCompleted(Response response) throws Exception {
				return (response.getStatus().getCode() == 200) ? handler.onSuccess(message, response) : handler.onFail(message, response);
			}

			public void onThrowable(Throwable ex) {
				handler.onThrow(message, ex);
			}
		});
	}
}
