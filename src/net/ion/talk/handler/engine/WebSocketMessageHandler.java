package net.ion.talk.handler.engine;

import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.talk.*;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

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
    public Reason onConnected(TalkEngine tengine, UserConnection uconn) {
		return Reason.OK ;
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {

        String response = null;
        ParameterMap params;
        Object scriptResult = null;
        try {

            if (tmsg.params() != null)
                params = ParameterMap.create(tmsg.params());
            else
                params = null;

            scriptResult = rengine.executePath(rsession, tmsg.id(), "/script/" + tmsg.script(), params);
            response = TalkResponseBuilder.makeResponse(tmsg.id(), scriptResult);

        } catch (IllegalArgumentException e1) {
            response = TalkResponseBuilder.makeResponse(e1);
            e1.printStackTrace();

        } catch (UnsupportedOperationException e2) {
            response = TalkResponseBuilder.makeResponse(e2);
            e2.printStackTrace();

        } catch (NullPointerException e3) {
            response = TalkResponseBuilder.makeResponse(e3);
            e3.printStackTrace();

        } finally {
            if(!JsonObject.fromString(response).get("result").toString().equals("\"undefined\"")){
                uconn.sendMessage(response);
            }

        }

    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws Exception {
        rengine = tengine.rhinoEntry();
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }




}
