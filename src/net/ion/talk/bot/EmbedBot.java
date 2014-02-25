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

    String onInvited(String roomId);
    String onExit(String roomId);
    String onUserEnter(String roomId, String userId);
    String onUserExit(String roomId, String userId);
    String onMessage(String roomId, String sender, String message);

}
