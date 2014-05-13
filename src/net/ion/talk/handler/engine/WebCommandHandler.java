package net.ion.talk.handler.engine;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpHead;

import net.ion.craken.node.ReadSession;
import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.StringUtil;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.script.CommandScript;

public class WebCommandHandler implements TalkHandler{

	private CommandScript cs;

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		return Reason.OK;
	}

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		
	}

	@Override
	public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
		if (! tmsg.isCommandUserMessage()) return ;
		
		String agent = uconn.request().header(HttpHeaders.USER_AGENT) ;
		String[] cmds = StringUtil.split(tmsg.userMessage(), " /") ;
		
		cs.outroomFn(cmds[0], uconn, ArrayUtil.subarray(cmds, 1, cmds.length)) ;
	}

	@Override
	public void onEngineStart(TalkEngine tengine) throws Exception {
		this.cs = CommandScript.create(tengine.readSession(), tengine.context().getAttributeObject(ScheduledExecutorService.class.getCanonicalName(), ScheduledExecutorService.class))
			.readDir(new File("./command"), true);
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
		
	}

}
