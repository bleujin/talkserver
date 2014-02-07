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
import org.apache.ecs.xhtml.script;
import org.mozilla.javascript.NativeJavaObject;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 5.
 * Time: 오후 3:55
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketMessageHandler implements TalkHandler {

    RhinoEntry rengine;

    @Override
    public void onConnected(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {

        String response = null;

        try {
            RhinoScript rscript = getScriptWithValidation(uconn, rsession, tmsg);
            Object scriptResult = runScript(rscript);
            response = makeResponse(tmsg.id(), scriptResult);

        } catch (IllegalArgumentException e1) {
            response = makeFailResponse(e1);
            e1.printStackTrace();

        } catch (UnsupportedOperationException e2){
            response = makeFailResponse(e2);
            e2.printStackTrace();

        } finally {
            uconn.sendMessage(response);

        }

    }


    @Override
    public void onEngineStart(TalkEngine tengine) throws IOException {
        rengine = tengine.rhinoEntry();
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }


    private Object runScript(RhinoScript rscript) {
        return rscript.exec(new ResponseHandler<Object>() {
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
    }

    private RhinoScript getScriptWithValidation(UserConnection uconn, ReadSession rsession, TalkMessage tmsg){

        String scriptPath = tmsg.script();
        String messageId = tmsg.id();
        String script;

        if(StringUtil.isEmpty(messageId))
            throw new IllegalArgumentException("Required messageId!");

        if(rsession.exists(scriptPath))
            script = rsession.pathBy(scriptPath).property("script").stringValue();
        else
            throw new IllegalArgumentException("cannot found script in craken path:" + scriptPath);

        RhinoScript rscript = rengine.newScript(uconn.id()).defineScript(script)
                .bind("session", rsession)
                .bind("rb", TalkResponseBuilder.create());

        if (tmsg.params() == null)
            rscript.bind("params", ParameterMap.create(JsonObject.create()));
        else
            rscript.bind("params", ParameterMap.create(tmsg.params()));

        return rscript;
    }

    private String makeResponse(String id, Object result) {
        BasicBuilder response = TalkResponseBuilder.create().newInner()
                .property("createAt", ToonServer.GMTTime())
                .property("id", id)
                .property("result", result);

        if (result instanceof Throwable)
            response.property("status", "failure");
        else
            response.property("status", "success");

        return response.build().toString();
    }

    private String makeFailResponse(Exception e) {
        return TalkResponseBuilder.create().newInner()
                .property("status", "failure")
                .property("result", e.toString())
                .property("createdAt", ToonServer.GMTTime())
                .build().toString();
    }

}
