package net.ion.talk.bot;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 5:44
 * To change this template use File | Settings | File Templates.
 */
public class TestEchoBot extends TestCase {

    private BotManager botManager;
    private EchoBot echoBot;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        botManager = BotManager.create();
        echoBot = new EchoBot();
        botManager.registerBot(echoBot);
    }

    public void testOnMessage(){
        String message = "I'm Echo Bot!";
        String response = botManager.onMessage(echoBot.id(), message, "ryuneeee");
        assertEquals(response, message);
    }

    @Override
    public void tearDown() throws Exception {

        botManager.unRegisterBot(echoBot);
        super.tearDown();
    }
}
