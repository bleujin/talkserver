package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;

public class DebugTalkHandler implements TalkHandler {

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		Debug.line(uconn.id() + " closed");
	}

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		Debug.line(uconn.id() + " connected");
		return Reason.OK;
	}

	@Override
	public void onEngineStart(TalkEngine tengine) {
		Debug.line("engine started");
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
		Debug.line("engine stopped");
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
		Debug.line(tmsg.toPlainMessage() + " received");
	}

}
