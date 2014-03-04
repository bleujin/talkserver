package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.talk.bean.Const;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 4.
 * Time: 오후 2:18
 * To change this template use File | Settings | File Templates.
 */
public class TestBotManager extends TestCase {

    private RepositoryEntry rentry;
    private ReadSession rsession;
    private BotManager botManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rentry = RepositoryEntry.test();
        rsession = rentry.login();
        botManager = BotManager.create(rsession);
    }

    @Override
    public void tearDown() throws Exception {
        rentry.shutdown();
        super.tearDown();
    }

    public void testBasic() throws Exception {

        FakeBot fakeBot = new FakeBot();

        botManager.registerBot(fakeBot);
        assertTrue(rsession.exists("/bots/fakeBot"));
        assertEquals("fakeBot", rsession.pathBy("/users/fakeBot").property(Const.User.UserId).stringValue());
        assertEquals("fakeBot", rsession.pathBy("/users/fakeBot").property(Const.Bot.Nickname).stringValue());
        assertEquals("페이크봇", rsession.pathBy("/users/fakeBot").property(Const.Bot.StateMessage).stringValue());
        assertEquals("http://localhost:9000/bot", rsession.pathBy("/users/fakeBot").property(Const.Bot.RequestURL).stringValue());

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
