package net.ion.message.push.sender.strategy;

import junit.framework.TestCase;
import net.ion.message.push.sender.Vendor;

public class TestStrategies {

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
			public Vendor vender(String targetId) {
				return Vendor.APPLE;
			}

			@Override
			public String deviceId(String targetId) {
				if ("airkjh".equals(targetId)) {
					return "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e8";
				} else {
					return "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e7";
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
			public Vendor vender(String targetId) {
				return Vendor.GOOGLE;
			}

			@Override
			public String deviceId(String targetId) {
				return "APA91bFz7oI4kgcK4dt12fOPcovNv9hPrhC5Q_eFRnEu79maAsJTgjJ2Jl-n3c3kl7aoWuiCk7F0vT9VTl5GJFzVM1mG0Dxwzm0dpo0amhyp6rwKGe2a8MyFTkaf9CnMOUVYHddkeoWyk3QiOglvjOqbvhXs73Yx2XNelT_AOoHeyRCkYF9ZUY0";
			}

		};
	}

}
