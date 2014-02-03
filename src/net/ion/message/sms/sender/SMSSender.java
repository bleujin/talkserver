package net.ion.message.sms.sender;

import net.ion.framework.util.Debug;
import net.ion.message.sms.message.PhoneMessage;
import net.ion.message.sms.response.MessagingResponse;
import net.ion.message.sms.response.ResponseHandler;
import net.ion.message.sms.util.MessageID;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.Response;
import org.restlet.data.Method;

import java.io.IOException;
import java.util.concurrent.Future;

public class SMSSender {

    private SMSConfig config ;

    private ResponseHandler<MessagingResponse> responseHandler = new ResponseHandler<MessagingResponse>() {
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

    public SMSSender(SMSConfig config) {
        this.config = config ;
    }

    public PhoneMessage newMessage(String receiverPhone) {
        PhoneMessage message = new PhoneMessage(MessageID.generate());

        message.setAttribute(this.config.getSenderPhoneKey(), receiverPhone);
        message.setAttribute("deptcode", this.config.getDeptCode());
        message.setAttribute("usercode", this.config.getUserCode());

        message.setSender(this);

        return message;
    }

    public Future<MessagingResponse> send(PhoneMessage message) throws IOException {
        return send(message, responseHandler);
    }

    public <T> Future<T> send(PhoneMessage message, final ResponseHandler<T> handler) throws IOException {

        checkValidity(message);

        Request request = toRequest(message);

        return executeAsync(handler, request);
    }

    private <T> Future<T> executeAsync(final ResponseHandler<T> handler, Request request) throws IOException {
        return this.config.getClient().executeRequest(request, new AsyncCompletionHandler<T>() {
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

    private void checkValidity(PhoneMessage message) {
        this.config.getValidator().checkValidity(message);
    }

    private Request toRequest(PhoneMessage message) {
        return message.toRequest(this.config.getHandlerURL(), Method.POST);
    }

    public boolean isDomesticMessage() {
        return this.config.isDomesticMessage();
    }
}
