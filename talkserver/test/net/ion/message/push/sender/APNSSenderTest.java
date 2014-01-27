package net.ion.message.push.sender;

public class APNSSenderTest extends BaseTest {

	private boolean isProduction = true;

	private APNSSender sender = APNSSender.create(KEY_STORE_PATH, PASSWORD, isProduction);

	public void testFirst() throws Exception {
        PushResponse response = sender.sendTo(APPLE_DEVICE_TOKEN).message("안녕").badge(1).sound("default").push();
        assertTrue(response.isSuccess());
	}

	public void testError_whenNoMsg() throws Exception {
		try {
			sender.sendTo(APPLE_DEVICE_TOKEN).push();
			fail();
		} catch (IllegalStateException e) {}
	}

	public void testError_tooLargeMessage() throws Exception {
        String largeMessage = createDummyMessage(840);
        try{
			sender.sendTo(APPLE_DEVICE_TOKEN).message(largeMessage.toString()).push();
			fail();
		} catch (IllegalStateException e) {}
	}

	public void testResponse() throws Exception {
		PushResponse response = sender.sendTo(APPLE_DEVICE_TOKEN).message("Hello World").push();
		assertEquals(true, response.isSuccess());
	}

	public void testFailResponse() throws Exception {
		PushResponse response = sender.sendTo(INVALID_DEVICE_TOKEN).message("This message cannot be sent").push();

		assertEquals(false, response.isSuccess());
		assertNotNull(response.getResponseMessage());
	}

}