package net.ion.message.sms.response;

import net.ion.message.sms.sender.PhoneMessage;
import net.ion.radon.aclient.Response;

public interface ResponseHandler<T> {
	
	public static ResponseHandler<Response> DefaultResponseHandler = new ResponseHandler<Response>() {
		@Override
		public Response onSuccess(PhoneMessage pmessage, Response response) {
			return response;
		}

		@Override
		public Response onFail(PhoneMessage pmessage, Response response) {
			return response;
		}

		@Override
		public void onThrow(PhoneMessage pmessage, Throwable t) {
			t.printStackTrace();
		}
	};

    public T onSuccess(PhoneMessage pmessage, Response response);
    public T onFail(PhoneMessage pmessage, Response response);
    public void onThrow(PhoneMessage pmessage, Throwable t);

}
