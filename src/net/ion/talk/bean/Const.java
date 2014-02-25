package net.ion.talk.bean;

public class Const {

	
	public static class User {
        public static final String UserId = "userId";
		public static final String AccessToken = "accessToken";
		public static final String DeviceOS = "deviceOS";
		public static final String PushId = "pushId";
		public static String DelegateServer = "delegateServer" ;
	}

    public static class Bot {
        public static final String BotId = "botId";
        public static final String RequestURL = "requestURL";
    }

    public static class Message {

        public static final String Event = "event";
        public static final String Message = "message";
        public static final String Sender = "sender";
        public static final String NotifyId = "notifyId";
        public static final String Receivers = "receivers";
    }

    public static class Room {
        public static final String RoomId = "roomId";
    }

    public static class Event {
        public static final String onInvited = "onInvited";
        public static final String onExit = "onExit";
        public static final String onUserEnter= "onUserEnter";
        public static final String onUserExit= "onUserExit";
        public static final String onMessage= "onMessage";
    }


}

