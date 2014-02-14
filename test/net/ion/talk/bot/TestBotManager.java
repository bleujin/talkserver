package net.ion.talk.bot;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오전 10:22
 * To change this template use File | Settings | File Templates.
 */
public class TestBotManager extends TestCase{

    private BotManager botManager;
    private EchoBot echoBot;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        echoBot = new EchoBot();
        botManager = BotManager.create();
        botManager.registerBot(echoBot);
    }

    public void testRegisterAndUnRegister() throws Exception {
        assertEquals(echoBot, botManager.findBy(echoBot.id()));
        botManager.unRegisterBot(echoBot);
        assertNull(botManager.findBy(echoBot.id()));
    }

    public void testOnInvited(){
        String response = botManager.onMessage(echoBot.id(), "ROOM|1|ENTER|EchoBot", "sender");
        assertEquals(response, "Everybody Hello! I'm Echo Bot!");

    }

    public void testEnterUser(){
        String response = botManager.onMessage(echoBot.id(), "ROOM|1|ENTER|ryuneeee", "ryuneeee");
        assertEquals(response, "Hello! ryuneeee");
    }

    public void testExitUser() throws Exception {
        String response = botManager.onMessage(echoBot.id(), "ROOM|1|EXIT|ryuneeee", "ryuneeee");
        assertEquals(response, "Bye! ryuneeee");
    }

    public void testOnExit() throws Exception {
        String response = botManager.onMessage(echoBot.id(), "ROOM|1|EXIT|EchoBot", "sender");
        assertEquals(response, "Everybody Bye!");
    }

    @Override
    public void tearDown() throws Exception {
        botManager.unRegisterBot(echoBot);
        super.tearDown();
    }
}
