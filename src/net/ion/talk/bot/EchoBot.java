package net.ion.talk.bot;

import net.ion.framework.parse.gson.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:26
 * To change this template use File | Settings | File Templates.
 */
public class EchoBot implements EmbedBot {
    @Override
    public String id() {
        return "echoBot";
    }

    @Override
    public JsonObject onInvited(String roomId) {
        return null;
    }

    @Override
    public JsonObject onExit(String roomId) {
        return null;
    }

    @Override
    public JsonObject onUserEnter(String roomId, String userId) {
        return null;
    }

    @Override
    public JsonObject onUserExit(String roomId, String userId) {
        return null;
    }

    @Override
    public JsonObject onMessage(String roomId, String sender, String message) {
        return null;
    }
}
