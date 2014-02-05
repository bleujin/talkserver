package net.ion.message.push.sender.strategy;

import net.ion.message.push.sender.BaseTest;
import net.ion.message.push.sender.Vender;

public class TestStrategies extends BaseTest{

	public static PushStrategy airkjhAPNSStrategy() {
		return new PushStrategy() {
			@Override
			public int getBadge() {
				return 1;
			}

			@Override
			public String getSound() {
				return "default";
			}

			@Override
			public int getTimeToLive() {
				return 60 * 30; // 30 minutes
			}

			@Override
			public String getCollapseKey() {
				return "test";
			}

			@Override
			public boolean getDelayWhenIdle() {
				return false;
			}

			@Override
			public Vender vender(String targetId) {
				return Vender.APPLE;
			}

			@Override
			public String deviceId(String targetId) {
				if ("airkjh".equals(targetId)) {
					return APPLE_DEVICE_TOKEN;
				} else {
					return INVALID_DEVICE_TOKEN;
				}
			}

		};
	}

	public static PushStrategy airkjhGoogleStrategy() {

		return new PushStrategy() {
			@Override
			public int getBadge() {
				return 1;
			}

			@Override
			public String getSound() {
				return "default";
			}

			@Override
			public int getTimeToLive() {
				return 60 * 30; // 30 minutes
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
				return Vender.GOOGLE;
			}

			@Override
			public String deviceId(String targetId) {
				return GOOGLE_DEVICE_TOKEN;
			}

		};
	}

}
