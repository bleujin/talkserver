package net.ion.talk.bean;

public class Const {

	
	public static class User {
        public static final String UserId = "userId";
		public static final String AccessToken = "accessToken";
		public static final String DeviceOS = "deviceOS";
		public static final String PushId = "pushId";
		public static final String Password = "password";
		public static String DelegateServer = "delegateServer" ;
	}

    public static class Bot {
        public static final String BotId = "botId";
        public static final String RequestURL = "requestURL";
        public static final String isSyncBot = "isSyncBot";
    }

    public static class Message {

        public static final String Event = "event";
        public static final String Message = "message";
        public static final String Sender = "sender";
        public static final String NotifyId = "notifyId";
        public static final String Receivers = "receivers";
        public static final String ClientScript = "clientScript";
        public static final String RequestId = "requestId";
        public static final String MessageId = "messageId";
        public static final String CausedEvent = "causedBy";
        public static final String Filter = "filter";
        public static final String FilterEnabled = "filterEnabled";
    }

    public static class Room {
        public static final String RoomId = "roomId";
    }

    public static class Event {
        public static final String onEnter = "onEnter";
        public static final String onExit = "onExit";
        public static final String onMessage= "onMessage";
        public static final String onFilter = "onFilter";
    }


    public static class Connection {
        public static final String DelegateServer = "delegateServer";
    }

    public static class Notify {
        public static final String LastNotifyId = "lastNotifyId";
        public static final String CreatedAt = "createdAt";
    }
}

