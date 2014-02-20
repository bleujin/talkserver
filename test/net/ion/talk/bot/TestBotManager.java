package net.ion.talk.bot;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:24
 * To change this template use File | Settings | File Templates.
 */
public class TestBotManager extends TestCase {


    private BotManager botManager;

    public void testRegisterAndUnregister() throws Exception {

        botManager = BotManager.create();
        Bot echoBot = new EchoBot();

        botManager.registerBot(echoBot);
        assertEquals(echoBot, botManager.getBot("echoBot"));

        botManager.unregisterBot(echoBot);
        assertNull(botManager.getBot("echoBot"));
    }
}
