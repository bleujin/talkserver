package net.ion.talk.handler.engine;

import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.talk.*;
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
    public void onConnected(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {

        String response = null;
        ParameterMap params;

        if(tmsg.params()!=null)
            params = ParameterMap.create(tmsg.params());
        else
            params = null;

        Object scriptResult = rengine.executePath(tmsg.id(), tmsg.script(), params);
        response = TalkResponseBuilder.makeResponse(tmsg.id(), scriptResult);

        uconn.sendMessage(response);

    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws IOException {
        rengine = tengine.rhinoEntry();
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }




}
