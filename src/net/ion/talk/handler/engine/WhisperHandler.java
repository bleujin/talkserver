package net.ion.talk.handler.engine;

import java.util.Date;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.TalkMessage.MType;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.bean.Const.Room;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.WhisperMessage;

public class WhisperHandler implements TalkHandler{

	private ReadSession session;
	private BotScript bscript;

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		return Reason.OK;
	}

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		
	}

	@Override
	public void onMessage(TalkEngine tengine, final UserConnection uconn, ReadSession rsession, final TalkMessage tmsg) {
		if (tmsg.messageType() == MType.COMMAND) { // /time -> @system time 
			final WhisperMessage whisper = WhisperMessage.create(tmsg) ;
			bscript.whisper(uconn, whisper) ;
			return ;
		}

		if (tmsg.messageType() != MType.WHISPER) return ;
		
		final WhisperMessage whisper = WhisperMessage.create(tmsg) ;
		final String userId = whisper.toUserId() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				if (wsession.exists("/bots/"+ userId)){
					bscript.whisper(uconn, whisper) ;
				} else if (wsession.exists("/users/" + userId)) {
					String msgId = new ObjectId().toString() ; 
					wsession.pathBy("/rooms/@" + userId + "/messages/" + msgId)
						.property(Message.Message, whisper.userMessage())
						.property(Message.MessageId, msgId)
						.refTo("sender", "/users/" + uconn.id())
						.property(Message.ClientScript, whisper.asString(Message.ClientScript))
						.property(Message.Options, "{event:'onWhispher'}")
						.property("fromRoomId",whisper.asString("roomId"))
						.property(Message.RequestId, whisper.asString(Message.RequestId))
						.property(Message.Time, String.valueOf(new Date().getTime()))
						.property(Message.Receivers, userId) ;
				}
				return null;
			}
		}) ;
		
	}

	@Override
	public void onEngineStart(TalkEngine tengine) throws Exception {
		this.session = tengine.readSession() ;
		this.bscript = tengine.context().getAttributeObject(BotScript.class.getCanonicalName(), BotScript.class) ;
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
		
	}

}
