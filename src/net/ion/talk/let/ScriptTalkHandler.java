package net.ion.talk.let;

import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.script.rhino.ResponseHandler;
import net.ion.script.rhino.RhinoScript;
import net.ion.talk.ParameterMap;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkHandler;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;

public class ScriptTalkHandler implements TalkHandler {

	@Override
	public void onClose(TalkEngine tengine, UserConnection uconn) {
		
	}

	@Override
	public void onConnected(TalkEngine tengine, UserConnection uconn) {

	}

    @Override
    public void onEngineStart(TalkEngine tengine) {

    }

    @Override
    public void onEngineStop(TalkEngine tengine) {

    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection sender, ReadSession rsession, TalkMessage tmessage) {

        Debug.line("onMessage: " + sender + rsession + tmessage);
        RhinoEntry rengine = tengine.context().getAttributeObject(RhinoEntry.EntryName, RhinoEntry.class);
        RhinoScript rscript = rengine.newScript(tmessage.id()).defineScript(tmessage.script());

        rscript.bind("session", rsession).bind("params", ParameterMap.create(tmessage.params())) ;
        String scriptResult = rscript.exec(ResponseHandler.StringMessage) ;
        sender.sendMessage(scriptResult) ;
    }

}
