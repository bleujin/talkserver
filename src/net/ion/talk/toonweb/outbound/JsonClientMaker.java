package net.ion.talk.toonweb.outbound;

import java.io.IOException;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;

public class JsonClientMaker extends DynamicProxyClientMaker {

    @Override
    public String createMessage(String methodName, Object[] args) throws IOException {
    	return JsonObject.create().put("action", methodName).put("args", new JsonArray().adds(args)).toString();
    	
//        Map<String, Object> outgoing = new HashMap<String, Object>();
//        outgoing.put("action", methodName);
//        outgoing.put("args", args);
//        StringWriter writer = new StringWriter();
//        JSON.writerWithDefaultPrettyPrinter().writeValue(writer, outgoing);
//        return writer.toString();
    }
}
