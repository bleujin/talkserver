package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.message.push.sender.Pusher;
import net.ion.message.push.sender.PusherConfig;
import net.ion.talk.handler.craken.NotifyStrategy;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.infinispan.util.concurrent.WithinThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 18.
 * Time: 오후 2:16
 * To change this template use File | Settings | File Templates.
 */
public class TestSender extends TestCase {

    private static String GCM_API_KEY = "AIzaSyBC_YDd2WfKy_K3T7r5PQo3M_dMfg5k5WA";
    private static String KEY_STORE_PATH = "./resource/keystore/toontalk.p12";
    private static String PASSWORD = "toontalk";

    public void testWithInThread() throws Exception {

        PusherConfig config = PusherConfig.newBuilder().googleConfig(GCM_API_KEY).appleConfig(KEY_STORE_PATH, PASSWORD, false).build();
//        Sender sender = Sender.create(new NotifyStrategy(null), Executors.newCachedThreadPool(), config);
        Pusher pusher = Pusher.create(config, new NotifyStrategy(null));
        pusher.sendTo("test").sendAsync("");

    }
}
