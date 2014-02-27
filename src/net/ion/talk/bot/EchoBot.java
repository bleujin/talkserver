package net.ion.talk.bot;

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
    public String onEnter(String roomId, String userId, String sender) {

        //if bot
        if(id().equals(userId)){

            return String.format("var memberList = session.pathBy('/rooms/%s/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/%s/messages/%s')\n" +
                    "\t.property('message', 'Hello I\\'m EchoBot')\n" +
                    "\t.property('sender', '%s')\n" +
                    "\t.property('roomId', '%s')\n" +
                    "\t.property('event', 'onMessage')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '%s')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '%s')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, roomId, new ObjectId().toString(), id(), roomId, new ObjectId().toString(), id());
        }else{

            return String.format("var memberList = session.pathBy('/rooms/%s/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/%s/messages/%s')\n" +
                    "\t.property('message', 'Hello! %s')\n" +
                    "\t.property('sender', '%s')\n" +
                    "\t.property('roomId', '%s')\n" +
                    "\t.property('event', 'onMessage')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '%s')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '%s')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, roomId, new ObjectId().toString(), userId, id(), roomId, new ObjectId().toString(), id());
        }
    }

    @Override
    public String onExit(String roomId, String userId, String sender) {

        //if bot
        if(id().equals(userId)){

            return String.format("var memberList = session.pathBy('/rooms/%s/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/%s/messages/%s')\n" +
                    "\t.property('message', 'Bye~ see you later!')\n" +
                    "\t.property('sender', '%s')\n" +
                    "\t.property('roomId', '%s')\n" +
                    "\t.property('event', 'onMessage')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '%s')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '%s')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, roomId, new ObjectId().toString(), id(), roomId, new ObjectId().toString(), id());
        }else{

            return String.format("var memberList = session.pathBy('/rooms/%s/members').childrenNames().toArray();\n" +
                    "\n" +
                    "session.tranSync(function(wsession){\n" +
                    "\tvar messageNode = wsession.pathBy('/rooms/%s/messages/%s')\n" +
                    "\t.property('message', 'Bye! %s')\n" +
                    "\t.property('sender', '%s')\n" +
                    "\t.property('roomId', '%s')\n" +
                    "\t.property('event', 'onMessage')\n" +
                    "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                    "\t.property('requestId', '%s')\n" +
                    "\n" +
                    "\tfor(i in memberList){\n" +
                    "\t\tif(memberList[i] != '%s')\n" +
                    "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                    "\t}\n" +
                    "});", roomId, roomId, new ObjectId().toString(), userId, id(), roomId, new ObjectId().toString(), id());
        }
    }

    @Override
    public String onMessage(String roomId, String sender, String message) {
        return String.format("var memberList = session.pathBy('/rooms/%s/members').childrenNames().toArray();\n" +
                "\n" +
                "session.tranSync(function(wsession){\n" +
                "\tvar messageNode = wsession.pathBy('/rooms/%s/messages/%s')\n" +
                "\t.property('message', '%s')\n" +
                "\t.property('sender', '%s')\n" +
                "\t.property('roomId', '%s')\n" +
                "\t.property('event', 'onMessage')\n" +
                "\t.property('clientScript', 'client.room().message(args.message)')\n" +
                "\t.property('requestId', '%s')\n" +
                "\n" +
                "\tfor(i in memberList){\n" +
                "\t\tif(memberList[i] != '%s')\n" +
                "\t\t\tmessageNode.append('receivers', memberList[i]);\n" +
                "\t}\n" +
                "});", roomId, roomId, new ObjectId().toString(), message, id(), roomId, new ObjectId().toString(), id());
    }




}
