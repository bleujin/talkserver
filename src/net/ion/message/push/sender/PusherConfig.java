package net.ion.message.push.sender;

import java.util.concurrent.ExecutorService;

import net.ion.message.push.sender.strategy.PushStrategy;

import org.infinispan.util.concurrent.WithinThreadExecutor;

public class PusherConfig {

    private String apnsKeyStore;
    private String apnsPassword;
    private boolean apnsIsProduction;
    private String googleAPIKey;

    private ExecutorService es;

    private PusherConfig(String apnsKeyStore, String apnsPassword, boolean apnsIsProduction, String googleAPIKey) {
    	this.apnsKeyStore = apnsKeyStore ;
    	this.apnsPassword = apnsPassword ;
    	this.apnsIsProduction = apnsIsProduction ;
    	this.googleAPIKey = googleAPIKey ;
    }

    public static SenderConfigBuilder newBuilder() {
        return new SenderConfigBuilder();
    }

    public static PusherConfig createTest() {
        return newBuilder().appleConfig("./resource/keystore/toontalk.p12", "toontalk", true).googleConfig("AIzaSyBC_YDd2WfKy_K3T7r5PQo3M_dMfg5k5WA").build();
    }

    public Pusher createPusher(PushStrategy strategy) {
        return Pusher.create(this, strategy);
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }

    public String getApnsKeyStore() {
        return apnsKeyStore;
    }

    public String getApnsPassword() {
        return apnsPassword;
    }

    public boolean isApnsIsProduction() {
        return apnsIsProduction;
    }

    public ExecutorService getExecutorService() {
        return es;
    }

    public static class SenderConfigBuilder {

        private String keystore;
        private String password;
        private boolean production;
        private String apiKey;
        private ExecutorService es;

        public SenderConfigBuilder appleConfig(String keystore, String password, boolean isProduction) {
            this.keystore = keystore;
            this.password = password;
            this.production = isProduction;
            return this;
        }

        public SenderConfigBuilder googleConfig(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public SenderConfigBuilder executor(ExecutorService es) {
            this.es = es;
            return this;
        }

        public PusherConfig build() {
            PusherConfig config = new PusherConfig(this.keystore, this.password, this.production, this.apiKey);
            if (this.es == null) {
                config.es = new WithinThreadExecutor();
            }

            return config;
        }

    }
}
