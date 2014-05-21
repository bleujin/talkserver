package net.ion.talk.bean;

public class Const {

	
	public static class User {
        public static final String UserId = "userId";
		public static final String AccessToken = "accessToken";
		public static final String DeviceOS = "deviceOS";
		public static final String PushId = "pushId";
		public static final String Password = "password";
		public static final String NickName = "nickname";
		public static final String StateMessage = "stateMessage";
//		public static String DelegateServer = "delegateServer" ;
		public static final String Phone = "phone";
	}

    public static class Bot {
        public static final String BotId = "botId";
        public static final String RequestURL = "requestURL";
        public static final String isSyncBot = "isSyncBot";
        public static final String Nickname = "nickname";
        public static final String StateMessage = "stateMessage";
    }

    public static class Message {

        public static final String Options = "options";
        public static final String Message = "message";
        public static final String Sender = "sender";
        public static final String NotifyId = "notifyId";
        public static final String Receivers = "receivers";
        public static final String ClientScript = "clientScript";

        public static final String DefaultOnMessageClientScript = "client.room().message(args);";

        public static final String RequestId = "requestId";
        public static final String MessageId = "messageId";
        public static final String CausedEvent = "causedBy";
        public static final String Filter = "filter";
        public static final String FilterEnabled = "filterEnabled";
		public static final String Time = "time";
		public static final String ExclusiveSender = "exclusiveSender" ;

    }

    public static class Room {
        public static final String RoomId = "roomId";
    }

    public static class Connection {
        public static final String DelegateServer = "delegateServer";
    }

    public static class Notify {
        public static final String LastNotifyId = "lastNotifyId";
        public static final String CreatedAt = "createdAt";
        public static final String NotifyId = "notifyId";
    }

    public class Ref {
        public static final String Bot = "bot";
        public static final String User = "user";
    }
    
    public static class Status {
    	public static final String Status = "status" ;
    	public static final String Success = "success" ;
    	public static final String Failure = "failure" ;
    }
}

