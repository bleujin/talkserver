package net.ion.talk.handler.craken;

import java.util.concurrent.ExecutorService;

import net.ion.craken.node.ReadSession;
import net.ion.message.push.sender.Pusher;
import net.ion.message.push.sender.PusherConfig;
import net.ion.message.push.sender.Vender;
import net.ion.message.push.sender.strategy.PushStrategy;
import net.ion.talk.bean.Const.User;

public class NotifyStrategy implements PushStrategy {

	private final ReadSession rsession;
	
	private static String GCM_API_KEY = "AIzaSyBC_YDd2WfKy_K3T7r5PQo3M_dMfg5k5WA";
	private static String KEY_STORE_PATH = "./resource/keystore/toontalk.p12";
	private static String PASSWORD = "toontalk";

	
	public NotifyStrategy(ReadSession rsession) {
		this.rsession = rsession;
	}
	
	public static Pusher createPusher(ExecutorService worker, ReadSession rsession){
		PusherConfig config = PusherConfig.newBuilder().googleConfig(GCM_API_KEY).appleConfig(KEY_STORE_PATH, PASSWORD, false).executor(worker).build();
        return Pusher.create(config, new NotifyStrategy(rsession));
	}

	@Override
	public int getBadge() {
		return 1;
	}

	@Override
	public String getSound() {
		return null;
	}

	@Override
	public int getTimeToLive() {
		return 0;
	}

	@Override
	public String getCollapseKey() {
		return null;
	}

	@Override
	public boolean getDelayWhenIdle() {
		return false;
	}

	@Override
	public Vender vender(String targetId) {
		return "apple".equals(rsession.pathBy("/users/" + targetId).property(User.DeviceOS ).stringValue()) ? Vender.APPLE :Vender.GOOGLE;
	}

	@Override
	public String deviceId(String targetId) {
		return rsession.pathBy("/users/" + targetId).property(User.PushId).stringValue();
	}
}