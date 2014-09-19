package net.ion.talk.handler.engine;

import java.util.Date;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.TalkMessage.MType;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.WhisperMessage;

import com.google.common.cache.Cache;

public class WhisperHandler implements TalkHandler {

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
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, final TalkMessage tmsg) {
		if (tmsg.messageType() != MType.WHISPER)
			return;
		final WhisperUserConnection wuconn = new WhisperUserConnection(tengine.context().getAttributeObject(Cache.class.getCanonicalName(), Cache.class), uconn);

		final WhisperMessage whisper = WhisperMessage.create(wuconn, tmsg);
		final String userId = whisper.toUserId();

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				if (wsession.exists("/bots/" + userId)) {
					bscript.whisper(wuconn, whisper);
				} else if (wsession.exists("/users/" + userId)) {
					wsession.pathBy("/rooms/@" + userId + "/messages/" + whisper.messageId()).property(Message.Message, whisper.message()).property(Message.MessageId, whisper.messageId()).refTo("sender", "/users/" + wuconn.id()).property(Message.ClientScript, whisper.asString(Message.ClientScript))
							.property(Message.Options, "{event:'onWhisper'}").property("fromRoomId", whisper.fromRoomId()).property(Message.RequestId, whisper.asString(Message.RequestId)).property(Message.Time, String.valueOf(new Date().getTime())).property(Message.Receivers, userId);
				}
				return null;
			}
		});

	}

	@Override
	public void onEngineStart(TalkEngine tengine) throws Exception {
		this.session = tengine.readSession();
		this.bscript = tengine.context().getAttributeObject(BotScript.class.getCanonicalName(), BotScript.class);
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {

	}

}


