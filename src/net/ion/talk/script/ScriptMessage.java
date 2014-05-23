package net.ion.talk.script;

import net.ion.talk.UserConnection;


public interface ScriptMessage {

	public String message();

	public String[] messages();

	public String toUserId();

	public boolean isNotInRoom();

	public MessageCommand asCommand();

	public String fromRoomId();

	public String fromUserId();

	public String messageId();

	UserConnection source();
}
