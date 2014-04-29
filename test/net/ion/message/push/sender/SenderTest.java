package net.ion.message.push.sender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javapns.notification.PushedNotifications;
import net.ion.framework.util.Debug;
import net.ion.message.push.sender.handler.BeforeSendHandler;
import net.ion.message.push.sender.handler.BlankPushResponseHandler;
import net.ion.message.push.sender.strategy.TestStrategies;

public class SenderTest extends BaseTest {

	private PusherConfig config = PusherConfig.createTest();
	private Pusher senderForAPNS = config.createPusher(TestStrategies.airkjhAPNSStrategy());
	private Pusher senderForGCM = config.createPusher(TestStrategies.airkjhGoogleStrategy());

	public void testFirstAPNSSync() throws Exception {
		final CountDownLatch kjhDown = new CountDownLatch(1) ;
		senderForAPNS.sendTo("airkjh").send("안녕", new BlankPushResponseHandler(){
			@Override
			public Void onAPNSSuccess(AppleMessage amsg, PushedNotifications results) {
				kjhDown.countDown(); 
				return null;
			}
		});
		kjhDown.await(); 
		

		final CountDownLatch jinDown = new CountDownLatch(1) ;
		senderForAPNS.sendTo("bleujin").send("안녕", new BlankPushResponseHandler(){ // bleujin is invalid user
			@Override
			public Void onAPNSFail(AppleMessage amsg, PushedNotifications results) {
				jinDown.countDown(); 
				return null;
			}
		});
		jinDown.await(); 
	}

	public void testApns_async() throws ExecutionException, InterruptedException {
		final CountDownLatch kjhDown = new CountDownLatch(1) ;
		senderForAPNS.sendTo("airkjh").sendAsync("안녕", new BlankPushResponseHandler(){
			@Override
			public Void onAPNSSuccess(AppleMessage amsg, PushedNotifications results) {
				assertEquals("airkjh",  amsg.receiver());
				return null;
			}
		}).get();
	}

	public void testGcm_async() throws ExecutionException, InterruptedException {
		Future<Boolean> result = senderForGCM.sendTo("airkjh").sendAsync("안녕", ConfirmResponseHandler);
		assertTrue(result.get());
	}

	public void testWhenFailed() throws ExecutionException, InterruptedException {
		String invalidUserId = "airkjh2";

		// we do not use google sender at this time, so just don't pass api key argument as null
		PusherConfig retryConfig = PusherConfig.newBuilder().appleConfig(KEY_STORE_PATH, PASSWORD, true).googleConfig("").build();

		Pusher sender = retryConfig.createPusher(TestStrategies.airkjhAPNSStrategy());
		Boolean result = sender.sendTo(invalidUserId).sendAsync("모두 실패!!", ConfirmResponseHandler).get();
		// invalid token is considered as failed request, not exception
		assertFalse(result);
	}


	public void testBeforeSendHandler() {
		senderForAPNS.bforeSendHandler(new BeforeSendHandler() {
			@Override
			public void handle(PushMessage message) {
				SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
				Debug.line("Request at ", sdf.format(new Date()));
			}
		});

		senderForAPNS.sendTo("airkjh").send("안녕들하십니까");

	}

}
