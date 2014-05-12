package net.ion.talk.script;

import net.ion.framework.util.ObjectId;


public class BotMessage {

	private String message;
	private String sender;
	private String roomId;
	private String clientScript;
	private String messageId;
	private String botId;
	private String newMsgId = new ObjectId().toString() ;

	public static BotMessage create() {
		return new BotMessage() ;
	}
	
	public String newMsgId(){
		return newMsgId ;
	}

	public String message() {
		return message;
	}

	public String sender() {
		return sender;
	}

	public String roomId() {
		return roomId;
	}

	public String clientScript() {
		return clientScript;
	}

	public String messageId() {
		return messageId;
	}

	public String botId() {
		return botId;
	}

	public BotMessage message(String message) {
		this.message = message;
		return this;
	}

	public BotMessage sender(String sender) {
		this.sender = sender;
		return this;
	}

	public BotMessage roomId(String roomId) {
		this.roomId = roomId;
		return this;
	}

	public BotMessage clientScript(String clientScript) {
		this.clientScript = clientScript;
		return this;
	}

	public BotMessage messageId(String messageId) {
		this.messageId = messageId;
		return this;
	}

	public BotMessage botId(String botId) {
		this.botId = botId;
		return this;
	}


}
