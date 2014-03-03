package net.ion.talk.bot;

import net.ion.framework.parse.gson.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:26
 * To change this template use File | Settings | File Templates.
 */
public interface EmbedBot {
    String id();
    String requestURL();

    void onEnter(String roomId, String userId) throws Exception;
    void onExit(String roomId, String userId) throws Exception;
    void onMessage(String roomId, String sender, String message) throws Exception;
    void onFilter(String roomId, String sender, String message, String messageId) throws Exception;

}
