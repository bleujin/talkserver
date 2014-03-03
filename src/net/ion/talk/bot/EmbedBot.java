package net.ion.talk.bot;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.parse.gson.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class EmbedBot {

    protected final ReadSession rsession;
    protected String id;
    protected String requestURL;
    protected ReadSession session;

    protected EmbedBot(String id, String requestURL, ReadSession rsession) {
        this.id = id;
        this.requestURL = requestURL;
        this.rsession = rsession;
    }

    public String id(){
        return id;
    }

    public String requestURL(){
        return requestURL;
    }

    abstract public boolean isSyncBot();

    public abstract void onEnter(String roomId, String userId) throws Exception;
    public abstract void onExit(String roomId, String userId) throws Exception;
    public abstract void onMessage(String roomId, String sender, String message) throws Exception;
    public abstract void onFilter(String roomId, String sender, String message, String messageId) throws Exception;

    protected void setRoomProperty(final String roomId, final String key, final Object value) throws Exception {
        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/" + roomId + "/bots/" + id).property(key, value);
                return null;
            }
        });

    }

    protected PropertyValue getRoomProperty(String roomId, String key){
        return rsession.ghostBy("/rooms/" + roomId + "/bots/" + id).property(key);
    }

    protected void setUserProperty(final String roomId, final String user, final String key, final Object value) throws Exception {
        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/" + roomId + "/bots/" + id + "/" + user).property(key, value);
                return null;
            }
        });
    }

    protected PropertyValue getUserProperty(String roomId, String user, String key){
        return rsession.ghostBy("/rooms/" + roomId + "/bots/" + id + "/" + user).property(key);
    }


}
