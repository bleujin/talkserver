package net.ion.message.push.sender.strategy;

import net.ion.framework.util.Debug;
import net.ion.message.push.sender.PushResponse;
import net.ion.message.push.sender.handler.ResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimePrintResponseHandler implements ResponseHandler<Void> {
    private SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");

    @Override
    public Void result() {

        Debug.line("Request finished at ", getCurrentTime());

        return null;
    }

    @Override
    public void onSuccess(PushResponse response) {

        Debug.line("Success at ", getCurrentTime());

    }

    @Override
    public void onFail(PushResponse response) {

        Debug.line("Failed to request at ", getCurrentTime());

    }

    @Override
    public void onThrow(String receiver, String token, Throwable t) {
        Debug.line("Exception throwed at ", getCurrentTime());
    }

    private String getCurrentTime() {
        return sdf.format(new Date());
    }

}
