package net.ion.message.sms.response;

import net.ion.framework.util.Debug;

public interface ResponseHandler<T> {
	
	public static ResponseHandler<MessagingResponse> DefaultResponseHandler = new ResponseHandler<MessagingResponse>() {
		@Override
		public MessagingResponse onSuccess(MessagingResponse response) {
			Debug.debug("[MSG_SEND_SUCCESS]", response.getResponse("euc-kr"));
			return response;
		}

		@Override
		public MessagingResponse onFail(MessagingResponse response) {
			Debug.debug("[MSG_SEND_FAILED]", response);
			return response;
		}

		@Override
		public void onThrow(Throwable t) {
			t.printStackTrace();
		}
	};

    public T onSuccess(MessagingResponse response);
    public T onFail(MessagingResponse response);
    public void onThrow(Throwable t);

}
