package net.ion.talk.script;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.talk.TalkMessage;

public class WhisperMessage {

	private TalkMessage tm;
	private String toUserId;
	private String userMessage;
	private MessageCommand messageCmd;
	
	private WhisperMessage(TalkMessage tm, String toUserId, String wmessage) {
		this.tm = tm;
		this.toUserId = toUserId ;
		this.userMessage = wmessage ;
		this.messageCmd = MessageCommand.create(userMessage) ;
	}

	public final static WhisperMessage create(TalkMessage tm) {
		
		if (tm.userMessage().startsWith("@")) {
			String to = StringUtil.substringBefore(tm.userMessage(), " ").substring(1) ;
			String wmessage = StringUtil.substringAfter(tm.userMessage(), " ") ; 
			return new WhisperMessage(tm, to, wmessage);
		} else if (tm.userMessage().startsWith("/")) {
			String wmessage = tm.userMessage().substring(1) ;
			return new WhisperMessage(tm, "system", wmessage);
		}
		throw new IllegalArgumentException() ;
	}
	
	public String id(){
		return tm.id() ;
	}

	public String toUserId() {
		return toUserId;
	}

	public String userMessage(){
		return userMessage ;
	}
	
	public MessageCommand asCommand() {
		return messageCmd;
	}

	public String sender() {
		return tm.params().asString("sender");
	}

	public String fromRoomId() {
		return tm.params().asString("roomId");
	}

	public boolean isNotInRoom(){
		return StringUtil.isBlank(fromRoomId()) ;
	}
	
	public String[] userMessages() {
		return StringUtil.split(userMessage(), " ");
	}

	public String asString(String name) {
		return tm.params().asString(name);
	}

	public JsonObject asJson() {
		return tm.params().asJson();
	}
	
	
	
}
