package net.ion.talk.handler.engine;

import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.talk.ParameterMap;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage.MType;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.bean.Const.Room;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.script.ScriptResponseHandler;
import net.ion.talk.script.TalkScript;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 5. Time: 오후 3:55 To change this template use File | Settings | File Templates.
 */
public class TalkScriptHandler implements TalkHandler {

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
		if (tmsg.messageType() == MType.ILLEGAL) {
			uconn.sendMessage("illegal message : " + tmsg.toPlainMessage());
			return ;
		}
		
		if (tmsg.messageType() != MType.NORMAL) return ;
		
		tscript.callFn(tmsg.script(), ObjectUtil.coalesce(tmsg.params(), ParameterMap.BLANK), new ScriptResponseHandler<Void>() {
			@Override
			public Void onSuccess(String fullName, ParameterMap pmap, Object result) {
				if (result == null ||  StringUtil.isBlank(result.toString()) || "undefined".equals(result)) return null ;
				
				JsonObject forSend = JsonObject.create()
							.put("id", tmsg.id())
							.put("status", "success")
							.put("result", result instanceof JsonElement ? (JsonElement)result : ObjectUtil.toString(result)).put("script", tmsg.script()).put("params", pmap.asJson()) ;
				uconn.sendMessage(forSend.toString()) ;
				return null ;
			}
			public Void onThrow(String fullName, ParameterMap pmap, Exception ex) {
				JsonObject forSend = JsonObject.create().put("id", tmsg.id()).put("status", "failure").put("result", ex.getMessage()).put("script", tmsg.script()).put("params", pmap.asJson());
				uconn.sendMessage(forSend.toString()) ;
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
