package net.ion.message.sms.response;

public interface ResponseHandler<T> {

    public T onSuccess(MessagingResponse response);
    public T onFail(MessagingResponse response);
    public void onThrow(Throwable t);

}
