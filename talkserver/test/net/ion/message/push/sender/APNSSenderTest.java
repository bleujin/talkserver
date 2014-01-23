package net.ion.message.push.sender;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class APNSSenderTest extends TestCase {

	private String sendTo = "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e8";
	private String keystore = "./resource/keystore/toontalk.p12";
	private String password = "toontalk";
	private boolean isProduction = true;

	private APNSSender sender = APNSSender.create(keystore, password, isProduction);

	public void testFirst() throws Exception {
		sender.newMessage(sendTo).message("안녕").badge(1).sound("default").push();
	}

	public void error_whenNoMsg() throws Exception {
		try {
			sender.newMessage(sendTo).push();
			fail();
		} catch (IllegalStateException e) {

		}
	}

	public void error_tooLargeMessage() throws Exception {
		try {
			String base = "가나다라마바사아자차카타파하가나다라마바사아자차카타파하";
			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < 10; i++) {
				builder.append(base);
			}

			Debug.line("Payload Size = ", builder.toString().getBytes().length);

			sender.newMessage(sendTo).message(builder.toString()).push();
			fail();
		} catch (IllegalStateException e) {

		}
	}

	public void testResponse() throws Exception {
		PushResponse response = sender.newMessage(sendTo).message("Hello World").push();

		assertEquals(true, response.isSuccess());
	}

	public void testFailResponse() throws Exception {
		String invalidToken = "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e7";

		PushResponse response = sender.newMessage(invalidToken).message("This message cannot be sent").push();

		assertEquals(false, response.isSuccess());
		assertNotNull(response.getResponseMessage());

		Debug.line(response.getResponseMessage());

	}

}