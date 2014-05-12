package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyValue;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 17.
 * Time: 오전 9:54
 * To change this template use File | Settings | File Templates.
 */
public class TestOldEmbedBot extends TestCrakenBase{

    private String roomId = "testRoom";
    private String user = "ryun";


    public void testUserProperty() throws Exception {

        String key = "Hello";
        String value = "안녕하세요";

        FakeBot fakeBot = new FakeBot();
        fakeBot.setUserProperty(roomId, user, key, value);
        PropertyValue pValue = fakeBot.getUserProperty(roomId, user, key);
        assertEquals(value, pValue.stringValue());

    }

    public void testRoomProperty() throws Exception {
        String key = "desc";
        String value = "테스트방";

        FakeBot fakeBot = new FakeBot();
        fakeBot.setRoomProperty(roomId, key, value);
        PropertyValue pValue = fakeBot.getRoomProperty(roomId, key);
        assertEquals(value, pValue.stringValue());
    }


    private class FakeBot extends EmbedBot{
        protected FakeBot() {
            super("fakeBot", "FakeBot", "stateMessage", "http://localhost", TestOldEmbedBot.this.rsession);
        }

        @Override
        public boolean isSyncBot() {
            return false;
        }

        @Override
        public void onEnter(String roomId, String userId) throws Exception {
        }

        @Override
        public void onExit(String roomId, String userId) throws Exception {
        }

        @Override
        public void onMessage(String roomId, String sender, String message) throws Exception {
        }

        @Override
        public void onFilter(String roomId, String sender, String message, String messageId) throws Exception {
        }
    }
}
