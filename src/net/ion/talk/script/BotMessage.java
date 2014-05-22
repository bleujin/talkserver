package net.ion.talk.script;

import net.ion.craken.node.ReadNode;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
import net.ion.talk.bean.Const;

public class BotMessage {

	private String botId;
	private String newMsgId = new ObjectId().toString();
	private MessageCommand messageCmd;
	private ReadNode messageNode;

	private BotMessage(String botId, ReadNode messageNode) {
		this.botId = botId;
		this.messageNode = messageNode;
		this.messageCmd = MessageCommand.create(messageNode.property(Const.Message.Message).asString());
	}

	public static BotMessage create(String botId, ReadNode notifyNode) {
		ReadNode messageNode = notifyNode.ref(Const.Message.Message);
		return new BotMessage(botId, messageNode);
	}

	public String newMsgId() {
		return newMsgId;
	}

	public String message() {
		return messageNode.property(Const.Message.Message).asString();
	}

	public MessageCommand asCommand() {
		return messageCmd;
	}

	public String sender() {
		return messageNode.ref(Const.Message.Sender).property(Const.User.UserId).asString();
	}

	
	public String roomId() {
		return messageNode.parent().parent().fqn().name();
	}

	public String clientScript() {
		return messageNode.property(Const.Message.ClientScript).asString();
	}

	public String messageId() {
		return messageNode.fqn().name();
	}

	public String botId() {
		return botId;
	}

	public String eventName() {
		return JsonObject.fromString(messageNode.property(Const.Message.Options).asString()).asString("event");
	}

	public boolean isBlank(String name) {
		return (!messageNode.hasProperty(name)) || (StringUtil.isBlank(messageNode.property(name).asString())) ;
	}
	
	public boolean isNotInRoom(){
		return isBlank("roomId") ;
	}
	
	public String asString(String name) {
		return messageNode.property(name).asString();
	}

	public String[] asStrings(String name) {
		return messageNode.property(name).asStrings();
	}

	public int asInt(String name) {
		return messageNode.property(name).asInt();
	}

	
	public String toString(){
		return "botId:" + botId() + ", node:" + messageNode.toString() ;
	}
}
