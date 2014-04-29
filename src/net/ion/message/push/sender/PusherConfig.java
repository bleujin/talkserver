package net.ion.message.push.sender;

import java.util.concurrent.ExecutorService;

import net.ion.framework.util.ObjectUtil;
import net.ion.message.push.sender.strategy.PushStrategy;

import org.infinispan.util.concurrent.WithinThreadExecutor;

public class PusherConfig {

    private final String apnsKeyStore;
    private final String apnsPassword;
    private final boolean apnsIsProduction;
    private final String googleAPIKey;

    private final ExecutorService es;

    private PusherConfig(ExecutorService es, String apnsKeyStore, String apnsPassword, boolean apnsIsProduction, String googleAPIKey) {
    	this.es = es ;
    	this.apnsKeyStore = apnsKeyStore ;
    	this.apnsPassword = apnsPassword ;
    	this.apnsIsProduction = apnsIsProduction ;
    	this.googleAPIKey = googleAPIKey ;
    }

    public static PusherConfigBuilder newBuilder() {
        return new PusherConfigBuilder();
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

    public static class PusherConfigBuilder {

        private String keystore;
        private String password;
        private boolean production;
        private String apiKey;
        private ExecutorService es;

        public PusherConfigBuilder appleConfig(String keystore, String password, boolean isProduction) {
            this.keystore = keystore;
            this.password = password;
            this.production = isProduction;
            return this;
        }

        public PusherConfigBuilder googleConfig(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public PusherConfigBuilder executor(ExecutorService es) {
            this.es = es;
            return this;
        }

        public PusherConfig build() {
            PusherConfig config = new PusherConfig(ObjectUtil.coalesce(this.es, new WithinThreadExecutor()), this.keystore, this.password, this.production, this.apiKey);

            return config;
        }

    }
}
