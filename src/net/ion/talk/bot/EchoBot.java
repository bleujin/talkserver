package net.ion.talk.bot;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.talk.bean.Const;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:26
 * To change this template use File | Settings | File Templates.
 */
public class EchoBot implements EmbedBot {

    private final ReadSession rsession;
    private String id = "echoBot";
    private String requestURL = "http://localhost:9000/bot";

    public EchoBot(ReadSession rsession) {
        this.rsession = rsession;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String requestURL() {
        return requestURL;
    }

    @Override
    public void onEnter(final String roomId, final String userId, String sender) throws Exception {

        //if bot
        if (id().equals(userId))
            sendMessage(roomId, "Hello I'm EchoBot");
        else
            sendMessage(roomId, "Hello! " + userId);
    }

    @Override
    public void onExit(final String roomId, final String userId, String sender) throws Exception {

        if (id().equals(userId))
            sendMessage(roomId, "Bye~ see you later!");
        else
            sendMessage(roomId, "Bye! " + userId);
    }

    @Override
    public void onMessage(final String roomId, String sender, final String message) throws Exception {
        sendMessage(roomId, message);
    }

    private void sendMessage(final String roomId, final String message) throws Exception {
        final Set<String> memberList = rsession.pathBy("/rooms/" + roomId + "/members").childrenNames();
        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                WriteNode messageNode = wsession.pathBy("/rooms/" + roomId + "/messages/" + new ObjectId().toString())
                        .property(Const.Message.Message, message)
                        .property(Const.Message.Sender, id())
                        .property(Const.Room.RoomId, roomId)
                        .property(Const.Message.Event, Const.Event.onMessage)
                        .property(Const.Message.ClientScript, "client.room().message(args.message)")
                        .property(Const.Message.RequestId, new ObjectId().toString());

                for (String member : memberList) {
                    if (member != id())
                        messageNode.append(Const.Message.Receivers, member);
                }
                return null;
            }
        });
    }


}
