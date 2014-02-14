package net.ion.talk.bot;

import net.ion.framework.util.Debug;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 5:45
 * To change this template use File | Settings | File Templates.
 */
public class EchoBot implements Bot {
    @Override
    public String id() {
        return "EchoBot";
    }

    @Override
    public String onInvited(String roomId) {
        return "Everybody Hello! I'm Echo Bot!";
    }

    @Override
    public String onEnterUser(String roomId, String userId) {
        return "Hello! " + userId;
    }

    @Override
    public String onExitUser(String roomId, String userId) {
        return "Bye! " + userId;
    }

    @Override
    public String onExit(String roomId) {
        return "Everybody Bye!";
    }

    @Override
    public String onMessage(String message, String sender) {
        return message;
    }
}
