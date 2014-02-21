package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.talk.TalkEngine;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.TalkEngine.Reason;

public class EchoHandler implements TalkHandler{

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
	}

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		return Reason.OK ;
	}

	@Override
	public void onEngineStart(TalkEngine tengine) {
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
		uconn.sendMessage(tmsg.toPlainMessage()) ;
	}


}
