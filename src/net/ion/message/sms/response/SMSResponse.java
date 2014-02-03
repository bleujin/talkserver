package net.ion.message.sms.response;

import net.ion.radon.aclient.Response;

public class SMSResponse {

    private Response response;
    private Exception exception;

    public boolean isOK() {
        return exception == null && response != null;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
