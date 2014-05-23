package net.ion.talk.script;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.fake.FakeUserConnection;

public class WhisperMessage implements ScriptMessage {

	private TalkMessage tm;
	private String toUserId;
	private String message;
	private MessageCommand messageCmd;
	private String messageId;
	private UserConnection source;
	
	private WhisperMessage(UserConnection source, TalkMessage tm, String toUserId, String wmessage) {
		this.source = source ;
		this.tm = tm;
		this.toUserId = toUserId ;
		this.message = wmessage ;
		this.messageCmd = MessageCommand.create(message) ;
		this.messageId = new ObjectId().toString() ;
	}
	
	public final static WhisperMessage test(TalkMessage tm) {
		return create(FakeUserConnection.fake(tm.params().asString("sender")), tm) ;
	}

	public final static WhisperMessage create(UserConnection source, TalkMessage tm) {
		if (tm.userMessage().startsWith("@")) {
			String toUserId = StringUtil.substringBefore(tm.userMessage(), " ").substring(1) ;
			String wmessage = StringUtil.substringAfter(tm.userMessage(), " ") ; 
			return new WhisperMessage(source, tm, toUserId, wmessage);
		}
		throw new IllegalArgumentException() ;
	}


	
	@Override
	public UserConnection source() {
		return source ;
	}
	
	@Override
	public String messageId() {
		return messageId;
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public String[] messages() {
		return StringUtil.split(message(), " ");
	}

	@Override
	public String fromUserId() {
		return tm.params().asString("sender");
	}

	@Override
	public MessageCommand asCommand() {
		return messageCmd;
	}

	@Override
	public String toUserId() {
		return toUserId;
	}

	@Override
	public String fromRoomId() {
		return tm.params().asString("roomId");
	}

	@Override
	public boolean isNotInRoom(){
		return StringUtil.isBlank(fromRoomId()) ;
	}
	

	
	
	
	
	public String requestId(){
		return tm.id() ;
	}

	public String asString(String name) {
		return tm.params().asString(name);
	}

	public JsonObject asJson() {
		return tm.params().asJson();
	}

	
}
