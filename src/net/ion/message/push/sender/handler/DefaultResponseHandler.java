package net.ion.message.push.sender.handler;

import com.google.common.collect.Lists;
import net.ion.message.push.sender.PushMessage;
import net.ion.message.push.sender.PushResponse;

import java.util.List;

public class DefaultResponseHandler implements ResponseHandler<List<PushResponse>> {

    private PushMessage message;
    private List<PushResponse> responses = Lists.newArrayList();

    private DefaultResponseHandler(PushMessage message) {
        this.message = message;
    }

    public static DefaultResponseHandler create(PushMessage message) {
        return new DefaultResponseHandler(message);
    }

    @Override
    public List<PushResponse> result() {
        return responses;
    }

    @Override
    public void onSuccess(PushResponse response) {
        responses.add(response);
    }

    @Override
    public void onFail(PushResponse response) {
        responses.add(response);
    }

    @Override
    public void onThrow(String receiver, String token, Throwable t) {
        t.printStackTrace();
    }
}
