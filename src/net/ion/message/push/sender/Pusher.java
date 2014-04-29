package net.ion.message.push.sender;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.ion.message.push.sender.handler.BeforeSendHandler;
import net.ion.message.push.sender.handler.PushResponseHandler;
import net.ion.message.push.sender.strategy.PushStrategy;

public class Pusher {

    private PushStrategy strategy;
    private PusherConfig config;

    private APNSSender apnsSender;
    private GCMSender gcmSender;
    private ExecutorService es;
    private BeforeSendHandler beforeHandler;

    protected Pusher(PushStrategy strategy, ExecutorService es, PusherConfig config) {
        this.strategy = strategy;
        this.es = es;
        this.config = config;
        this.gcmSender = GCMSender.create(config.getGoogleAPIKey());
        this.apnsSender = APNSSender.create(config.getApnsKeyStore(), config.getApnsPassword(), config.isApnsIsProduction());
    }

    public static Pusher create(PusherConfig sconfig, PushStrategy strategy) {
        return new Pusher(strategy, sconfig.getExecutorService(), sconfig);
    }

    public PushMessage sendTo(String receiver) {
        return new PushMessage(this, receiver);
    }

    public <T> Future<T> send(final PushMessage pushMessage, final PushResponseHandler<T> handler) {

        Callable<T> sendTask = new Callable<T>() {
            @Override
            public T call() throws Exception {
            	String receiver  = pushMessage.getReceiver() ;
                String token = strategy.deviceId(receiver);

                PushResponse response = null;

                if(beforeHandler != null) {
                    beforeHandler.handle(pushMessage);
                }

                if (strategy.vender(receiver).isApple()) {
                    return apnsSender.sendTo(receiver, token).message(pushMessage.getMessage()).badge(strategy.getBadge()).sound(strategy.getSound()).push(handler);
                } else if (strategy.vender(receiver).isGoogle()) {
                    return gcmSender.sendTo(receiver, token).message(pushMessage.getMessage()).delayWhenIdle(strategy.getDelayWhenIdle()).timeToLive(strategy.getTimeToLive()).collapseKey(strategy.getCollapseKey()).push(handler);
                } else {
                    throw new IllegalArgumentException("not supported vendor : " + strategy.vender(receiver));
                }
            }
        };

        return es.submit(sendTask);
    }

    public void setBeforeSendHandler(BeforeSendHandler handler) {
        this.beforeHandler = handler;
    }
}