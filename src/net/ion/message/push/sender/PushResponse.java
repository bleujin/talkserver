package net.ion.message.push.sender;

import com.google.android.gcm.server.Result;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import javapns.notification.PushedNotification;

import java.util.List;

public class PushResponse {

    private boolean success = true;
    private String responseMessage = "";

    private List<Throwable> occuredExceptions = Lists.newArrayList();

    private String receiver;
    private PushResponse(String receiver){
    	this.receiver = receiver ;
    }
    
    public String receiver(){
    	return receiver ;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public static PushResponse fromAPNS(AppleMessage amsg, PushedNotification result) {
        PushResponse response = new PushResponse(amsg.receiver());
        response.success = result.isSuccessful();

        if (!response.success) {
            response.responseMessage = result.getResponse().getMessage();
        }

        return response;
    }

    public static PushResponse fromGoogle(GoogleMessage gmsg, Result result) {
        PushResponse response = new PushResponse(gmsg.receiver());

        response.success = result.getMessageId() != null;
        if(response.success) {
            response.responseMessage = result.getMessageId();
        } else {
            response.responseMessage = result.getErrorCodeName();
        }

        return response;
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("isSuccess", success)
                .add("responseMessage", responseMessage)
                .add("exceptions", occuredExceptions)
                .toString();
    }
}
