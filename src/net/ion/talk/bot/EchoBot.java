package net.ion.talk.bot;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectId;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:26
 * To change this template use File | Settings | File Templates.
 */
public class EchoBot implements EmbedBot {

    private String id = "echoBot";
    private String requestURL = "http://localhost:9000/bot";

    @Override
    public String id() {
        return id;
    }

    @Override
    public String requestURL() {
        return requestURL;
    }

    @Override
    public String onInvited(String roomId) {
        return "session.tranSync(function(wsession){\n" +
                "   wsession.pathBy(\"/rooms/"+roomId+"/messages/"+ new ObjectId().toString() +"\")\n" +
                ".property(\"message\",\"Hello! I'm, EchoBot!\")\n" +
                ".property(\"sender\",\""+id()+"\")\n" +
                ".property(\"clientScript\",\"client.room().message(args.message)\")\n" +
                ".property(\"requestId\",\""+ new ObjectId().toString() +"\");\n" +
                "});\n";
    }

    @Override
    public String onExit(String roomId) {
        return "session.tranSync(function(wsession){\n" +
                "   wsession.pathBy(\"/rooms/"+roomId+"/messages/"+ new ObjectId().toString() +"\")\n" +
                ".property(\"message\",\"Bye~\")\n" +
                ".property(\"sender\",\""+id()+"\")\n" +
                ".property(\"clientScript\",\"client.room().message(args.message)\")\n" +
                ".property(\"requestId\",\""+ new ObjectId().toString() +"\");\n" +
                "});\n";
    }

    @Override
    public String onUserEnter(String roomId, String userId) {
        return "session.tranSync(function(wsession){\n" +
                "   wsession.pathBy(\"/rooms/"+roomId+"/messages/"+ new ObjectId().toString() +"\")\n" +
                ".property(\"message\",\"Hello! "+userId+"\")\n" +
                ".property(\"sender\",\""+id()+"\")\n" +
                ".property(\"clientScript\",\"client.room().message(args.message)\")\n" +
                ".property(\"requestId\",\""+ new ObjectId().toString() +"\");\n" +
                "});\n";
    }

    @Override
    public String onUserExit(String roomId, String userId) {
        return "session.tranSync(function(wsession){\n" +
                "   wsession.pathBy(\"/rooms/"+roomId+"/messages/"+ new ObjectId().toString() +"\")\n" +
                ".property(\"message\",\"Bye! "+userId+"\")\n" +
                ".property(\"sender\",\""+id()+"\")\n" +
                ".property(\"clientScript\",\"client.room().message(args.message)\")\n" +
                ".property(\"requestId\",\""+ new ObjectId().toString() +"\");\n" +
                "});\n";
    }

    @Override
    public String onMessage(String roomId, String sender, String message) {
        return "session.tranSync(function(wsession){\n" +
                "   wsession.pathBy(\"/rooms/"+roomId+"/messages/"+ new ObjectId().toString() +"\")\n" +
                ".property(\"message\",\""+message+"\")\n" +
                ".property(\"sender\",\""+id()+"\")\n" +
                ".property(\"clientScript\",\"client.room().message(args.message)\")\n" +
                ".property(\"requestId\",\""+ new ObjectId().toString() +"\");\n" +
                "});\n";
    }
}
