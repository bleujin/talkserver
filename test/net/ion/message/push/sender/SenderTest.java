package net.ion.message.push.sender;

import net.ion.framework.util.Debug;
import net.ion.message.push.sender.handler.BeforeSendHandler;
import net.ion.message.push.sender.strategy.TestStrategies;
import net.ion.message.push.sender.strategy.TimePrintResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SenderTest extends BaseTest {

	private SenderConfig config = SenderConfig.createTest();
	private Pusher senderForAPNS = config.createSender(TestStrategies.airkjhAPNSStrategy());
	private Pusher senderForGCM = config.createSender(TestStrategies.airkjhGoogleStrategy());

	public void testFirst_apns_sync() throws Exception {
		List<PushResponse> response = senderForAPNS.sendTo("airkjh", "bleujin").send("안녕");

		assertEquals(2, response.size());
		assertEquals(true, response.get(0).isSuccess());
		assertEquals(false, response.get(1).isSuccess()); // bleujin is invalid user
	}

	public void testApns_async() throws ExecutionException, InterruptedException {
		Future<List<PushResponse>> future = senderForAPNS.sendTo("airkjh").sendAsync("안녕");
		List<PushResponse> responses = future.get();

		assertEquals(1, responses.size());
		assertEquals(true, responses.get(0).isSuccess());
	}

	public void testGcm_async() throws ExecutionException, InterruptedException {
		Future<List<PushResponse>> future = senderForGCM.sendTo("airkjh").sendAsync("안녕");
		List<PushResponse> responses = future.get();

		assertEquals(1, responses.size());
		assertEquals(true, responses.get(0).isSuccess());
	}

	public void testRetryWhenFailed() throws ExecutionException, InterruptedException {
		String invalidUserId = "airkjh2";
		int retryCount = 5;

		// we do not use google sender at this time, so just don't pass api key argument as null
		SenderConfig retryConfig = SenderConfig.newBuilder().appleConfig(KEY_STORE_PATH, PASSWORD, true).googleConfig("").retryAttempts(retryCount).build();

		Pusher sender = retryConfig.createSender(TestStrategies.airkjhAPNSStrategy());
		Map<String, Integer> attempts = sender.sendTo(invalidUserId).sendAsync("모두 실패!!", testResponseHandler).get();
		// invalid token is considered as failed request, not exception

		assertEquals(retryCount, attempts.get("failCount").intValue());
	}

	public void testRetryWhenException() throws ExecutionException, InterruptedException {
		int retryCount = 3;

		SenderConfig config = SenderConfig.newBuilder().googleConfig(INVALID_DEVICE_TOKEN).retryAttempts(retryCount).build();
		Pusher sender = config.createSender(TestStrategies.airkjhGoogleStrategy());
		Map<String, Integer> exceptionCount = sender.sendTo("airkjh").sendAsync("실패!!", testResponseHandler).get();

		// invalid api key(google) occures exception
		assertEquals(retryCount, exceptionCount.get("exceptionCount").intValue());
	}

	public void testRetryInterval() {
		SenderConfig config = SenderConfig.newBuilder().googleConfig(INVALID_DEVICE_TOKEN).retryAttempts(3).retryAfter(20, TimeUnit.SECONDS).build();
		Pusher sender = config.createSender(TestStrategies.airkjhGoogleStrategy());
		sender.sendTo("airkjh").sendAsync("Hello World!!", new TimePrintResponseHandler());
	}

	public void testSendSchedule() throws InterruptedException {
		SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
		senderForAPNS.sendTo("airkjh").sendSchedule("안녕", 10, TimeUnit.SECONDS, new TimePrintResponseHandler());
	}

	public void testBeforeSendHandler() {

		senderForAPNS.setBeforeSendHandler(new BeforeSendHandler() {
			@Override
			public void handle(PushMessage message) {
				SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
				Debug.line("Request at ", sdf.format(new Date()));
			}
		});

		senderForAPNS.sendTo("airkjh").send("안녕들하십니까");

	}

}
