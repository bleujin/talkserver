package net.ion.talk.handler;

import net.ion.craken.node.ReadSession;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;

public interface TalkHandler {

	Reason onConnected(TalkEngine tengine, UserConnection uconn) ;

	void onClose(TalkEngine tengine, UserConnection uconn);

	void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg);

	void onEngineStart(TalkEngine tengine) throws Exception;

	void onEngineStop(TalkEngine tengine);

}
