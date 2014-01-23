package net.ion.message.push.sender;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class GCMSenderTest extends TestCase {

	String sendTo = "APA91bFz7oI4kgcK4dt12fOPcovNv9hPrhC5Q_eFRnEu79maAsJTgjJ2Jl-n3c3kl7aoWuiCk7F0vT9VTl5GJFzVM1mG0Dxwzm0dpo0amhyp6rwKGe2a8MyFTkaf9CnMOUVYHddkeoWyk3QiOglvjOqbvhXs73Yx2XNelT_AOoHeyRCkYF9ZUY0";
	String apiKey = "AIzaSyCB3YWgx-2ECRJ0sHIlcMvrb6gOfRIQo88";

	GCMSender sender = GCMSender.create(apiKey);

	public void testFirst() throws IOException {
		PushResponse push = sender.newMessage(sendTo).message("안녕").delayWhenIdle(false).timeToLive(60 * 30).collapseKey("msg").push();

		assertEquals(push.isSuccess(), true);
	}

	public void error_whenNoMsg() throws Exception {
		try {
			sender.newMessage(sendTo).push();
			fail();
		} catch (IllegalStateException e) {

		}
	}

	public void testError_tooLargeMessage() throws Exception {
		try {
			String largeMessage = createLargeMessage();
			Debug.line("Payload Size = ", largeMessage.getBytes().length);

			sender.newMessage(sendTo).message(largeMessage).push();
			fail();
		} catch (IllegalStateException e) {

		}
	}

	private String createLargeMessage() throws UnsupportedEncodingException {
		// return 12270 bytes message
		String base = "가";
		StringBuilder builder = new StringBuilder();
		// 1366
		for (int i = 0; i < 4090; i++) {
			builder.append(base);
		}

		return builder.toString();
	}

	public void testResponse() throws Exception {
		PushResponse response = sender.newMessage(sendTo).message("Hello World").push();

		Debug.line(response);
		assertEquals(true, response.isSuccess());
		assertNotNull(response.getResponseMessage());
	}

	public void testFailResponse() throws Exception {
		String invalidToken = "abcd";

		PushResponse response = sender.newMessage(invalidToken).message("This message cannot be sent").push();

		Debug.line(response.getResponseMessage());

		assertEquals(false, response.isSuccess());
		assertNotNull(response.getResponseMessage());
	}

}
