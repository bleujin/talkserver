package net.ion.talk.bot;

import java.util.Set;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.util.ObjectId;
import net.ion.talk.bean.Const;


public abstract class EmbedBot {

    protected final ReadSession rsession;
    protected String id;
    protected String requestURL;
    protected ReadSession session;
    protected String nickname;
    protected String stateMessage;

    protected EmbedBot(String id, String nickname, String stateMessage, String requestURL, ReadSession rsession) {
        this.id = id;
        this.nickname = nickname;
        this.stateMessage = stateMessage;
        this.requestURL = requestURL;
        this.rsession = rsession;
    }

    public String id(){
        return id;
    }

    public String requestURL(){
        return requestURL;
    }

    public String nickname(){
        return nickname;
    }

    public String stateMessage(){
        return stateMessage;
    }

    abstract public boolean isSyncBot();

    public abstract void onEnter(String roomId, String userId) throws Exception;
    public abstract void onExit(String roomId, String userId) throws Exception;
    public abstract void onMessage(String roomId, String sender, String message) throws Exception;
    public abstract void onFilter(String roomId, String sender, String message, String messageId) throws Exception;

    protected void setRoomProperty(final String roomId, final String key, final Object value) throws Exception {
        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/" + roomId + "/bots/" + id).property(key, value);
                return null;
            }
        });

    }

    protected PropertyValue getRoomProperty(String roomId, String key){
        return rsession.ghostBy("/rooms/" + roomId + "/bots/" + id).property(key);
    }

    protected void setUserProperty(final String roomId, final String user, final String key, final Object value) throws Exception {
        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/" + roomId + "/bots/" + id + "/" + user).property(key, value);
                return null;
            }
        });
    }

    protected PropertyValue getUserProperty(String roomId, String user, String key){
        return rsession.ghostBy("/rooms/" + roomId + "/bots/" + id + "/" + user).property(key);
    }

    protected void sendMessage(final String roomId, String sender, final String message) throws Exception {

        final Set<String> memberList = rsession.pathBy("/rooms/" + roomId + "/members").childrenNames();

        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                String randomId = new ObjectId().toString();
                WriteNode messageNode = wsession.pathBy("/rooms/" + roomId + "/messages/" + randomId)
                        .property(Const.Message.Message, message)
                        .property(Const.Message.Sender, id())
                        .property(Const.Room.RoomId, roomId)
                        .property(Const.Message.Options, "{event:'onMessage'}")
                        .property(Const.Message.ClientScript, Const.Message.DefaultOnMessageClientScript)
                        .property(Const.Message.MessageId, randomId);

                for (String member : memberList) {
                    if (!member.equals(id()))
                        messageNode.append(Const.Message.Receivers, member);
                }
                return null;
            }
        });

    }


}
