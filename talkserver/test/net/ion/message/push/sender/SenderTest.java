package net.ion.message.push.sender;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.message.push.sender.handler.BeforeSendHandler;
import net.ion.message.push.sender.handler.ResponseHandler;
import net.ion.message.push.sender.strategy.TestStrategies;
import net.ion.message.push.sender.strategy.TimePrintResponseHandler;

public class SenderTest extends TestCase{

    private SenderConfig config = SenderConfig.createTest();
    private Sender senderForAPNS = config.createSender(TestStrategies.airkjhAPNSStrategy());
    private Sender senderForGCM = config.createSender(TestStrategies.airkjhGoogleStrategy());

    public void testFirst_apns_sync() throws Exception {
        List<PushResponse> response = senderForAPNS.createMessage("airkjh", "bleujin").send("안녕");

        assertEquals(2, response.size());
        assertEquals(true, response.get(0).isSuccess());
        assertEquals(false, response.get(1).isSuccess());               // bleujin is invalid user
    }

    public void testApns_async() throws ExecutionException, InterruptedException {
        Future<List<PushResponse>> future = senderForAPNS.createMessage("airkjh").sendAsync("안녕");
        List<PushResponse> responses = future.get();

        assertEquals(1, responses.size());
        assertEquals(true, responses.get(0).isSuccess());
    }

    public void testGcm_async() throws ExecutionException, InterruptedException {
        Future<List<PushResponse>> future = senderForGCM.createMessage("airkjh").sendAsync("안녕");
        List<PushResponse> responses = future.get();

        assertEquals(1, responses.size());
        assertEquals(true, responses.get(0).isSuccess());
    }

    public void testRetryWhenFailed() throws ExecutionException, InterruptedException {
        String invalidUserId = "airkjh2";
        int retryCount = 5;

        SenderConfig retryConfig = SenderConfig.newBuilder()
                .appleConfig("./resource/keystore/toontalk.p12", "toontalk", true)
                .googleConfig("")               // we do not use google sender at this time, so just don't pass api key argument as null
                .retryAttempts(retryCount)
                .build();

        Sender sender = retryConfig.createSender(TestStrategies.airkjhAPNSStrategy());

        Future<Integer> attempts = sender.createMessage(invalidUserId).sendAsync("모두 실패!!", new ResponseHandler<Integer>() {
            int failCount = 0;

            @Override
            public Integer result() {
                return failCount;
            }

            @Override
            public void onSuccess(PushResponse response) {
            }

            @Override
            public void onFail(PushResponse response) {
                failCount++;
            }

            @Override
            public void onThrow(String receiver, String token, Throwable t) {

            }
        });

        // invalid token is considered as failed request, not exception
        assertEquals(retryCount, attempts.get().intValue());
    }

    public void testRetryWhenException() throws ExecutionException, InterruptedException {
        int retryCount = 3;

        SenderConfig config = SenderConfig.newBuilder().googleConfig("invalid_api_key").retryAttempts(retryCount).build();
        Sender sender = config.createSender(TestStrategies.airkjhGoogleStrategy());

        Future<Integer> exceptionCount = sender.createMessage("airkjh").sendAsync("실패!!", new ResponseHandler<Integer>() {

            int exceptionCount = 0;

            @Override
            public Integer result() {
                return exceptionCount;
            }

            @Override
            public void onSuccess(PushResponse response) {
            }

            @Override
            public void onFail(PushResponse response) {
            }

            @Override
            public void onThrow(String receiver, String token, Throwable t) {
                exceptionCount++;
            }
        });


        // invalid api key(google) occures exception
        assertEquals(retryCount, exceptionCount.get().intValue());
    }

    public void testRetryInterval() {
        SenderConfig config = SenderConfig.newBuilder().googleConfig("invalid_api_key")
                .retryAttempts(3)
                .retryAfter(20, TimeUnit.SECONDS)
                .build();

        Sender sender = config.createSender(TestStrategies.airkjhGoogleStrategy());

        sender.createMessage("airkjh").sendAsync("Hello World!!", new TimePrintResponseHandler());
    }


    public void testSendSchedule() throws InterruptedException {

        SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
        Debug.line("Runned at ", sdf.format(new Date()));

        senderForAPNS.createMessage("airkjh").sendSchedule("안녕", 10, TimeUnit.SECONDS, new TimePrintResponseHandler());
    }

    public void testBeforeSendHandler() {

        senderForAPNS.setBeforeSendHandler(new BeforeSendHandler() {
            @Override
            public void handle(PushMessage message) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
                Debug.line("Request at ", sdf.format(new Date()));
            }
        });

        senderForAPNS.createMessage("airkjh").send("안녕들하십니까");

    }



}
