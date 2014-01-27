package net.ion.message.push.sender;

import java.io.IOException;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;

public class GCMTest extends BaseTest{

    public void testSimpleTest_withGCMLib() {

        com.google.android.gcm.server.Sender sender = new com.google.android.gcm.server.Sender(CGM_API_KEY);

        Message message = new Message.Builder()
                .collapseKey("1")
                .timeToLive(3)
                .delayWhileIdle(true)
                .addData("message", "hello world, this text will be seen in notification bar!!").build();

        try {
            Result result = sender.send(message, GOOGLE_DEVICE_TOKEN, 1);
            assertNotNull(result.toString());
        } catch (Exception e) {}
    }

    public void testTooLargeMsg() throws IOException {

        String largeMsg = createDummyMessage(12270);

        Message message = new Message.Builder()
                .addData("message", largeMsg)
                .delayWhileIdle(false)
                .timeToLive(60)
                .build();

        com.google.android.gcm.server.Sender sender = new com.google.android.gcm.server.Sender(CGM_API_KEY);
        Result result = sender.sendNoRetry(message, GOOGLE_DEVICE_TOKEN);

        // 12240 => send success
        // 12270 => send fail ( MessageToBig )
        // let's just assume 12240 bytes is limit

        assertEquals("MessageTooBig", result.getErrorCodeName());
    }


}
