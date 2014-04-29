package net.ion.message.push.sender;

import java.io.IOException;

public class GCMSenderTest extends BaseTest {

	GCMSender sender = GCMSender.create(GCM_API_KEY);

	public void testFirst() throws IOException {
		Boolean result = sender.sendTo("airkjh", GOOGLE_DEVICE_TOKEN).message("message").delayWhenIdle(false).timeToLive(60 * 30).collapseKey("msg").push(IsSuccessResponseHandler);
		assertTrue(result);
	}

	public void testError_whenNoMsg() throws Exception {
		Boolean result = sender.sendTo("airkjh", GOOGLE_DEVICE_TOKEN).push(IsSuccessResponseHandler);
		assertEquals(true, result == null); // onThrow
	}

	public void testError_tooLargeMessage() throws Exception {
		String largeMessage = createDummyMessage(12270);
		Boolean result = sender.sendTo("airkjh", GOOGLE_DEVICE_TOKEN).message(largeMessage).push(IsSuccessResponseHandler);
		assertEquals(true, result == null); // onThrow
	}

}
