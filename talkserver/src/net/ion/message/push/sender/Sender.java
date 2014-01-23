package net.ion.message.push.sender;

import net.ion.message.push.sender.handler.BeforeSendHandler;
import net.ion.message.push.sender.handler.ResponseHandler;
import net.ion.message.push.sender.strategy.PushStrategy;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Sender {

    private PushStrategy strategy;
    private SenderConfig config;

    private APNSSender apnsSender;
    private GCMSender gcmSender;
    private ExecutorService es;
    private BeforeSendHandler beforeHandler;

    private Sender(PushStrategy strategy, ExecutorService es, SenderConfig config) {
        this.strategy = strategy;
        this.es = es;
        this.config = config;
    }

    private Sender() {

    }

    public static Sender create(PushStrategy strategy, ExecutorService es, SenderConfig provider) {
        return new Sender(strategy, es, provider);
    }

    public static Sender create(SenderConfig config, PushStrategy strategy) {
        Sender sender = new Sender();
        sender.config = config;
        sender.strategy = strategy;
        sender.es = config.getExecutorService();
        sender.gcmSender = GCMSender.create(config.getGoogleAPIKey());
        sender.apnsSender = APNSSender.create(config.getApnsKeyStore(), config.getApnsPassword(), config.isApnsIsProduction());

        return sender;
    }

    public PushMessage createMessage(String... receivers) {
        return new PushMessage(this, receivers);
    }

    public <T> Future<T> send(final PushMessage pushMessage, final ResponseHandler<T> handler) {
        Callable<T> sendTask = new Callable<T>() {
            @Override
            public T call() throws Exception {

                long retryIntervalInMillis = config.getRetryAfter() * 1000;

                for (String receiver : pushMessage.getReceivers()) {
                    String token = strategy.deviceId(receiver);
                    int failCount = 0;

                    PushResponse response = null;

                    if(beforeHandler != null) {
                        beforeHandler.handle(pushMessage);
                    }

                    do {
                        try {
                            response = doSend(receiver, token);

                            if (response.isSuccess()) {
                                handler.onSuccess(response);
                                break;
                            } else if (!response.isSuccess()) {
                                failCount++;
                                handler.onFail(response);
                            }

                        } catch (Throwable t) {
                            failCount++;
                            handler.onThrow(receiver, token, t);
                        }

                        if(shouldRetry(failCount)) {
                            Thread.sleep(retryIntervalInMillis);
                        }

                    } while (shouldRetry(failCount));
                }

                return handler.result();
            }

            private boolean shouldRetry(int failCount) {
                return failCount < config.getRetryCount();
            }

            private PushResponse doSend(String receiver, String token) throws Exception {
                PushResponse response = null;

                if (strategy.vender(receiver).isApple()) {
                    response = apnsSender.newMessage(token)
                            .message(pushMessage.getMessage())
                            .badge(strategy.getBadge())
                            .sound(strategy.getSound()).push();
                } else if (strategy.vender(receiver).isGoogle()) {
                    response = gcmSender.newMessage(token)
                            .message(pushMessage.getMessage())
                            .delayWhenIdle(strategy.getDelayWhenIdle())
                            .timeToLive(strategy.getTimeToLive())
                            .collapseKey(strategy.getCollapseKey())
                            .push();
                } else {
                    throw new IllegalArgumentException("not supported vendor");
                }

                return response;
            }
        };

        return es.submit(sendTask);
    }

    public void setBeforeSendHandler(BeforeSendHandler handler) {
        this.beforeHandler = handler;
    }
}