package net.ion.talk.handler.engine;

import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.script.rhino.ResponseHandler;
import net.ion.script.rhino.RhinoScript;
import net.ion.talk.*;
import net.ion.talk.script.BasicBuilder;
import net.ion.talk.script.TalkResponseBuilder;
import org.mozilla.javascript.NativeJavaObject;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 5.
 * Time: 오후 3:55
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketTalkMessageHandler implements TalkHandler {

    RhinoEntry rengine;

    @Override
    public void onConnected(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {

        String execScript = null;

        //validation request parameter
        try {
            if (StringUtil.isEmpty(tmsg.script()) || StringUtil.isEmpty(tmsg.id()))
                throw new IllegalArgumentException();

            execScript = rsession.ghostBy(tmsg.script()).property("script").stringValue();
            if(StringUtil.isEmpty(execScript))
                throw new IllegalArgumentException();

        } catch (Exception e) {
            String response = TalkResponseBuilder.create().newInner()
                    .property("status", "failure")
                    .property("result", "Cannot found script or scriptId")
                    .property("createdAt", System.currentTimeMillis()).build().toString();
            uconn.sendMessage(response);
            return;
        }

        RhinoScript rscript = rengine.newScript(uconn.id()).defineScript(execScript)
                .bind("session", rsession)
                .bind("rb", TalkResponseBuilder.create());

        if (tmsg.params() == null)
            rscript.bind("params", ParameterMap.create(JsonObject.create()));
        else
            rscript.bind("params", ParameterMap.create(tmsg.params()));

        Object result = rscript.exec(new ResponseHandler<Object>() {
            @Override
            public Object onSuccess(RhinoScript script, Object rtnValue, long elapsedTime) {
                if (rtnValue instanceof NativeJavaObject)
                    return ((NativeJavaObject) rtnValue).unwrap();
                else
                    return rtnValue;
            }

            @Override
            public Object onFail(RhinoScript script, Throwable ex, long elapsedTime) {
                return ex;
            }
        });

        BasicBuilder response = TalkResponseBuilder.create().newInner().property("createAt", System.currentTimeMillis()).property("id", tmsg.id()).property("result", result);

        if (result instanceof Throwable)
            response.property("status", "failure");
        else
            response.property("status", "success");

        uconn.sendMessage(response.build().toString());
    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws IOException {
        rengine = tengine.rhinoEntry();
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }
}
