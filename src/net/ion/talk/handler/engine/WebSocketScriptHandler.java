package net.ion.talk.handler.engine;

import net.ion.craken.node.ReadSession;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.talk.ParameterMap;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.script.ScriptExceptionHandler;
import net.ion.talk.script.ScriptSuccessHandler;
import net.ion.talk.script.TalkScript;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 5. Time: 오후 3:55 To change this template use File | Settings | File Templates.
 */
public class WebSocketScriptHandler implements TalkHandler {

	private TalkScript tscript;

	@Override
	public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		return Reason.OK;
	}

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
	}

	@Override
	public void onMessage(TalkEngine tengine, final UserConnection uconn, ReadSession rsession, final TalkMessage tmsg) {
		if (!tmsg.isScript()) {
			uconn.sendMessage(tmsg.toPlainMessage()); // echo
			return;
		}
		
		tscript.callFn(tmsg.script(), ObjectUtil.coalesce(tmsg.params(), ParameterMap.BLANK), new ScriptSuccessHandler<Void>() {
			@Override
			public Void success(Object result) {
				if (result == null ||  StringUtil.isBlank(result.toString())) return null ;
				uconn.sendMessage(tmsg.successMesage(result)) ;
				return null ;
			}
		}, new ScriptExceptionHandler<Void>(){
			@Override
			public Void ehandle(Exception ex, String fullFnName, ParameterMap params) {
				uconn.sendMessage(tmsg.failMesage(ex)) ;
				return null;
			}
		});
	}

	@Override
	public void onEngineStart(TalkEngine tengine) {
		tscript = tengine.talkScript();
	}

	@Override
	public void onEngineStop(TalkEngine tengine) {
	}

}
