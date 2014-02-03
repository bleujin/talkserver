package net.ion.message.sms.response;

import net.ion.radon.aclient.Response;

import java.io.IOException;

public class MessagingResponse {

    private Response response;

    public String getResponse(String encoding) {
        try {
            return this.response.getTextBody(encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getReponse() {
        return getResponse("UTF-8");
    }

    public static MessagingResponse from(Response response) {
        MessagingResponse resp = new MessagingResponse();
        resp.response = response;

        return resp;
    }
}
