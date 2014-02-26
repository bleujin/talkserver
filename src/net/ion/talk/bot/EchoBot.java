package net.ion.talk.bot;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectId;

import java.text.MessageFormat;

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
    public String onEnter(String roomId, String userId) {

        //if bot
        if(id().equals(userId)){

            return MessageFormat.format("var memberList = session.pathBy('/rooms/{0}/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/{0}/messages/{2}')\n" +
                    "\t.property('message', 'Hello I\\'m EchoBot')\n" +
                    "\t.property('sender', '{1}')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '{2}')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '{1}')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, id(), new ObjectId().toString());
        }else{

            return MessageFormat.format("var memberList = session.pathBy('/rooms/{0}/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/{0}/messages/{2}')\n" +
                    "\t.property('message', 'Hello! {3}')\n" +
                    "\t.property('sender', '{1}')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '{2}')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '{1}')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, id(), new ObjectId().toString(), userId);
        }
    }

    @Override
    public String onExit(String roomId, String userId) {

        //if bot
        if(id().equals(userId)){

            return MessageFormat.format("var memberList = session.pathBy('/rooms/{0}/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/{0}/messages/{2}')\n" +
                    "\t.property('message', 'Bye~ see you later!')\n" +
                    "\t.property('sender', '{1}')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '{2}')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '{1}')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, id(), new ObjectId().toString());
        }else{

            return MessageFormat.format("var memberList = session.pathBy('/rooms/{0}/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/{0}/messages/{2}')\n" +
                    "\t.property('message', 'bye! {3}')\n" +
                    "\t.property('sender', '{1}')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '{2}')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '{1}')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, id(), new ObjectId().toString(), userId);
        }
    }

    @Override
    public String onMessage(String roomId, String sender, String message) {

//        if(message.equals("도움말"))
//            return MessageFormat.format("var memberList = session.pathBy('/rooms/{0}/members').childrenNames().toArray();\n" +
//                    "\n" +
//                    "session.tranSync(function(wsession){\n" +
//                    "\tvar messageNode = wsession.pathBy('/rooms/{0}/messages/{2}')\n" +
//                    "\t.property('message', '도움말입니다. \"반응 3초\", \"반응 5초\"라고 하면 유저별로 3초, 5초 후에 반응합니다.')\n" +
//                    "\t.property('sender', '{1}')\n" +
//                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
//                    "\t.property('requestId', '{2}')\n" +
//                    "\n" +
//                    "\tfor(i in memberList){\n" +
//                    "\t\tif(memberList[i] != '{1}')\n" +
//                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
//                    "\t}\n" +
//                    "});", roomId, id(), new ObjectId().toString());
//
//        return MessageFormat.format("var memberList = session.pathBy('/rooms/{0}/members').childrenNames().toArray();\n" +
//                "\n" +
//                "session.tranSync(function(wsession){\n" +
//                "\tvar messageNode = wsession.pathBy('/rooms/{0}/messages/{2}')\n" +
//                "\t.property('message', 'bye! {3}')\n" +
//                "\t.property('sender', '{1}')\n" +
//                "\t.property('clientScript', 'client.room().message(args.message)')\n" +
//                "\t.property('requestId', '{2}')\n" +
//                "\n" +
//                "\tfor(i in memberList){\n" +
//                "\t\tif(memberList[i] != '{1}')\n" +
//                "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
//                "\t}\n" +
//                "});", roomId, id(), new ObjectId().toString(), sender);
        return null;
    }

}
