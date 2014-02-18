package net.ion.talk;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.talk.TalkEngine.Reason;

public interface TalkHandler {

	Reason onConnected(TalkEngine tengine, UserConnection uconn) ;

	void onClose(TalkEngine tengine, UserConnection uconn);

	void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg);

	void onEngineStart(TalkEngine tengine) throws IOException;

	void onEngineStop(TalkEngine tengine);

}
