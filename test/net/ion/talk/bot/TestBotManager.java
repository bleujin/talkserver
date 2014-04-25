package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 4.
 * Time: 오후 2:18
 * To change this template use File | Settings | File Templates.
 */
public class TestBotManager extends TestCrakenBase{

    private BotManager botManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        botManager = BotManager.create(rsession);
    }

    public void testRegisterBot() throws Exception {

        FakeBot fakeBot = new FakeBot();

        botManager.registerBot(fakeBot);
        assertTrue(rsession.exists("/bots/fakeBot"));
        assertEquals(fakeBot, botManager.getBot("fakeBot"));
        botManager.unregisterBot(fakeBot);
        assertTrue(!rsession.exists("/bots/fakeBot"));
    }

    private class FakeBot extends EmbedBot {

        protected FakeBot() {
            super("fakeBot", "fakeBot", "페이크봇", "http://localhost:9000/bot", null);
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String requestURL() {
            return requestURL;
        }

        @Override
        public boolean isSyncBot() {
            return false;
        }

        @Override
        public void onEnter(String roomId, String userId) {
        }

        @Override
        public void onExit(String roomId, String userId) {
        }

        @Override
        public void onMessage(String roomId, String sender, String message) {
        }

        @Override
        public void onFilter(String roomId, String sender, String message, String messageId) throws Exception {
        }
    }
}
