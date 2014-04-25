package net.ion.message.push.sender;

import net.ion.message.push.sender.strategy.PushStrategy;
import org.infinispan.util.concurrent.WithinThreadExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SenderConfig {

    private String googleAPIKey;
    private String apnsKeyStore;
    private String apnsPassword;
    private boolean apnsIsProduction;

    private ExecutorService es;
    private int retryCount;
    private long retryAfter;

    private SenderConfig() {
    }

    public static SenderConfigBuilder newBuilder() {
        return new SenderConfigBuilder();
    }

    public static SenderConfig createTest() {
        return newBuilder().appleConfig("./resource/keystore/toontalk.p12", "toontalk", true).googleConfig("AIzaSyBC_YDd2WfKy_K3T7r5PQo3M_dMfg5k5WA").build();
    }

    public static SenderConfig createRetryTestConfig(int retryCount) {
        return newBuilder().appleConfig("./talkserver/resource/keystore/toontalk.p12", "toontalk", true)
                .googleConfig("AIzaSyBC_YDd2WfKy_K3T7r5PQo3M_dMfg5k5WA")
                .retryAttempts(retryCount)
                .build();
    }

    public Pusher createSender(PushStrategy strategy) {
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

    public int getRetryCount() {
        return retryCount;
    }

    public long getRetryAfter() {
        return retryAfter;
    }

    public static class SenderConfigBuilder {

        private String keystore;
        private String password;
        private boolean production;
        private String apiKey;
        private ExecutorService es;
        private int retryCount = 0;             // default config is that don't retry when failed
        private long retryInterval = 10;
        private TimeUnit intervalUnit = TimeUnit.SECONDS;

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

        public SenderConfigBuilder retryAttempts(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public SenderConfigBuilder retryAfter(long interval, TimeUnit unit) {
            this.retryInterval = interval;
            this.intervalUnit = unit;
            return this;
        }

        public SenderConfig build() {
            SenderConfig config = new SenderConfig();

            config.apnsKeyStore = keystore;
            config.apnsPassword = password;
            config.apnsIsProduction = production;
            config.googleAPIKey = apiKey;
            config.retryCount = retryCount;
            config.retryAfter = getRetryIntervalInSec();

            if (this.es == null) {
                config.es = new WithinThreadExecutor();
            }

            return config;
        }

        private long getRetryIntervalInSec() {
            return TimeUnit.SECONDS.convert(retryInterval, intervalUnit);
        }

    }
}
