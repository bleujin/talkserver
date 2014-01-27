package net.ion.message.push.sender;

import net.ion.framework.util.Debug;

import java.io.IOException;

public class GCMSenderTest extends BaseTest {


	GCMSender sender = GCMSender.create(CGM_API_KEY);

	public void testFirst() throws IOException {
		PushResponse push = sender.sendTo(GOOGLE_DEVICE_TOKEN).message("안녕").delayWhenIdle(false).timeToLive(60 * 30).collapseKey("msg").push();
		assertTrue(push.isSuccess());
	}

	public void testError_whenNoMsg() throws Exception {
		try {
			sender.sendTo(GOOGLE_DEVICE_TOKEN).push();
			fail();
		} catch (IllegalStateException e) {}
	}

	public void testError_tooLargeMessage() throws Exception {
		try {

			String largeMessage = createDummyMessage(12270);
            Debug.line(largeMessage.length());
            sender.sendTo(GOOGLE_DEVICE_TOKEN).message(largeMessage).push();
			fail();
		} catch (IllegalStateException e) {}
	}

	public void testResponse() throws Exception {
		PushResponse response = sender.sendTo(GOOGLE_DEVICE_TOKEN).message("Hello World").push();
		assertTrue(response.isSuccess());
		assertNotNull(response.getResponseMessage());
	}

	public void testFailResponse() throws Exception {
		PushResponse response = sender.sendTo(INVALID_DEVICE_TOKEN).message("This message cannot be sent").push();
		assertFalse(response.isSuccess());
		assertNotNull(response.getResponseMessage());
	}

}
