package net.ion.talk.handler.engine;

import java.util.logging.Logger;

import javax.script.ScriptException;

import net.ion.craken.node.IteratorList;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.StringUtil;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.TalkMessage.MType;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.Bot;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.MessageCommand;
import net.ion.talk.script.ScriptMessage;

public class PreHandler implements TalkHandler {

	private BotScript tscript;
	private Logger logger = LogBroker.getLogger(PreHandler.class) ;
	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		return Reason.OK;
	}

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		
	}

	@Override
	public void onMessage(TalkEngine tengine, final UserConnection sender, ReadSession rsession, final TalkMessage tmsg) {
		if (tmsg.messageType() != MType.NORMAL) return ;
		
		final String msgId = new net.ion.framework.util.ObjectId().toString();
		tmsg.resetParam(Message.MessageId, msgId);
		tmsg.resetParam("_message", tmsg.params().asString(Const.Message.Message)) ;
		
		String roomId = tmsg.params().asString(Const.Room.RoomId) ;
		IteratorList<ReadNode> iter = rsession.ghostBy("/rooms/" + roomId).refChildren(Bot.PreBots).iterator() ;
		while(iter.hasNext()){
			final ReadNode botNode = iter.next() ;
			try {
				final String botId = botNode.fqn().name();
				tmsg.setParam("_pre", botId) ;

				tscript.callFrom(botId, "onPre", new ScriptMessage(){
					
					private MessageCommand messageCmd = MessageCommand.create(tmsg.userMessage());
					@Override
					public String message() {
						return tmsg.userMessage();
					}
					
					public ScriptMessage resetParam(String name, String value){
						tmsg.resetParam(name, value) ;
						return this ;
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
					public boolean isNotInRoom() {
						return StringUtil.isBlank(tmsg.params().asString("roomId"));
					}

					@Override
					public MessageCommand asCommand() {
						return messageCmd;
					}

					@Override
					public String fromRoomId() {
						return tmsg.params().asString("roomId");
					}

					@Override
					public String fromUserId() {
						return tmsg.params().asString("sender");
					}

					@Override
					public String messageId() {
						return msgId;
					}

					@Override
					public UserConnection source() {
						return sender;
					}
				}) ;
			} catch (ScriptException e) {
				logger.warning(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEngineStart(TalkEngine tengine) throws Exception {
		tscript = tengine.botScript() ;
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
		
	}

}
