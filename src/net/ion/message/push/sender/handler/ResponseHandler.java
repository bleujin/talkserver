package net.ion.message.push.sender.handler;

import net.ion.message.push.sender.PushResponse;

public interface ResponseHandler<T> {

    public <T> T result() ;

    public void onSuccess(PushResponse response) ;

    public void onFail(PushResponse response) ;

    public void onThrow(String receiver, String token, Throwable t) ;
}
