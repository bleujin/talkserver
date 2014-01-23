package net.ion.message.push.sender;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;

public class GCMTest extends TestCase{

    String sendTo = "APA91bFz7oI4kgcK4dt12fOPcovNv9hPrhC5Q_eFRnEu79maAsJTgjJ2Jl-n3c3kl7aoWuiCk7F0vT9VTl5GJFzVM1mG0Dxwzm0dpo0amhyp6rwKGe2a8MyFTkaf9CnMOUVYHddkeoWyk3QiOglvjOqbvhXs73Yx2XNelT_AOoHeyRCkYF9ZUY0";
    String apiKey = "AIzaSyCB3YWgx-2ECRJ0sHIlcMvrb6gOfRIQo88";

    public void testSimpleTest_withGCMLib() {

        com.google.android.gcm.server.Sender sender = new com.google.android.gcm.server.Sender(apiKey);


        Message message = new Message.Builder()
                .collapseKey("1")
                .timeToLive(3)
                .delayWhileIdle(true)
                .addData("message", "hello world, this text will be seen in notification bar!!").build();

        Result result;

        try {
            result = sender.send(message, sendTo, 1);
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testTooLargeMsg() throws IOException {

        String largeMsg = createLargeMessage();

        Debug.line(largeMsg.getBytes("UTF-8").length);


        Message message = new Message.Builder()
                .addData("message", largeMsg)
                .delayWhileIdle(false)
                .timeToLive(60)
                .build();

        com.google.android.gcm.server.Sender sender = new com.google.android.gcm.server.Sender(apiKey);

        Result result = sender.sendNoRetry(message, sendTo);

        // 12240 => send success
        // 12270 => send fail ( MessageToBig )
        // let's just assume 12240 bytes is limit

        assertEquals(result.getErrorCodeName(), "MessageTooBig");
    }

    private String createLargeMessage() throws UnsupportedEncodingException {
        // return 12270 bytes message
        String base = "가";
        StringBuilder builder = new StringBuilder();
        //1366
        for(int i = 0; i < 4090; i++) {
            builder.append(base);
        }

        return builder.toString();
    }

}
