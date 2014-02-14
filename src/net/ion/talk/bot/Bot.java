package net.ion.talk.bot;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 4:23
 * To change this template use File | Settings | File Templates.
 */
public interface Bot {

    String id();

    String onInvited(String roomId);
    String onEnterUser(String roomId, String userId);
    String onExitUser(String roomId, String userId);
    String onExit(String roomId);


    String onMessage(String message, String sender);
}
