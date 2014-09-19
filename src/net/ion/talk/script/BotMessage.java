package net.ion.talk.script;

import net.ion.craken.node.ReadNode;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;

public class BotMessage implements ScriptMessage{

	private String botId;
	
	private MessageCommand messageCmd;
	private ReadNode messageNode;
	private UserConnection source;

	private BotMessage(UserConnection source, String botId, ReadNode messageNode) {
		this.source = source ;
		this.botId = botId;
		this.messageNode = messageNode;
		this.messageCmd = MessageCommand.create(messageNode.property(Const.Message.Message).asString());
	}

	public static BotMessage create(UserConnection source, String botId, ReadNode notifyNode) {
		ReadNode messageNode = notifyNode.ref(Const.Message.Message);
		return new BotMessage(source, botId, messageNode);
	}

	
	@Override 
	public UserConnection source(){
		return source ;
	}

	@Override
	public String message() {
		return messageNode.property(Const.Message.Message).asString();
	}

	@Override
	public String[] messages() {
		return StringUtil.split(message(), " ");
	}


	@Override
	public String toUserId() {
		return botId;
	}

	@Override
	public boolean isNotInRoom(){
		return false ;
	}

	@Override
	public MessageCommand asCommand() {
		return messageCmd;
	}

	@Override
	public String fromRoomId() {
		return messageNode.parent().parent().fqn().name();
	}

	@Override
	public String fromUserId() {
		return messageNode.ref(Const.Message.Sender).property(Const.User.UserId).asString();
	}

	@Override
	public String messageId() {
		return messageNode.fqn().name();
	}	
	
	
	
	
	
	
	public String clientScript() {
		return messageNode.property(Const.Message.ClientScript).asString();
	}

	public String eventName() {
		return JsonObject.fromString(messageNode.property(Const.Message.Options).asString()).asString("event");
	}

	public boolean isBlank(String name) {
		return (!messageNode.hasProperty(name)) || (StringUtil.isBlank(messageNode.property(name).asString())) ;
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
		return "botId:" + toUserId() + ", node:" + messageNode.toString() ;
	}
}
