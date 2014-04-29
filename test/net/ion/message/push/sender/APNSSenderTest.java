package net.ion.message.push.sender;


public class APNSSenderTest extends BaseTest {

	private boolean isProduction = true;

	private APNSSender sender = APNSSender.create(KEY_STORE_PATH, PASSWORD, isProduction);

	public void testFirst() throws Exception {
		Boolean response = sender.sendTo("airkjh", APPLE_DEVICE_TOKEN).message("안녕").badge(1).sound("default").push(IsSuccessResponseHandler);
		assertTrue(response);
	}

	public void testError_whenNoMsg() throws Exception {
		Boolean result = sender.sendTo("airkjh",APPLE_DEVICE_TOKEN).push(IsSuccessResponseHandler);
		assertEquals(true, result == null); // onThrow
	}

	public void testError_tooLargeMessage() throws Exception {
		String largeMessage = createDummyMessage(840);
		Boolean result = sender.sendTo("airkjh",APPLE_DEVICE_TOKEN).message(largeMessage.toString()).push(IsSuccessResponseHandler);
		assertEquals(true, result == null); // onThrow
	}

}