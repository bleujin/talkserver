package net.ion.talk.toonweb.inbound;

import java.io.IOException;

import net.ion.framework.parse.gson.Gson;

public class JsonInboundDispatcher extends InboundDispatcher {
    private static final Gson GSON = new Gson() ;
    public JsonInboundDispatcher(Object server, Class<?> clientType) {
        super(server, clientType);
    }

    @Override
    protected InboundMessage unmarshalInboundRequest(String msg) throws IOException {
    	return GSON.fromJson(msg, ActionArgsTuple.class) ;
//        return JSON.readValue(msg, ActionArgsTuple.class);
    }

    public static class ActionArgsTuple implements InboundMessage {
        public String action;
        public Object[] args;

        @Override
        public String method() {
            return action;
        }

        @Override
        public Object[] args() {
            return args;
        }
    }

}
