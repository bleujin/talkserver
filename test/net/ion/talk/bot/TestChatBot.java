package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.StringUtil;
import net.ion.talk.bean.Const;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 25.
 * Time: 오전 11:34
 * To change this template use File | Settings | File Templates.
 */
public class TestChatBot extends TestCrakenBase{

    private ChatBot chatBot;

    private String roomId = "test";
    private String sender = "ryun";
    private String messageId = "testMessage";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        chatBot = new ChatBot(rsession);

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/members/chatBot");
                return null;
            }
        });
    }
    
    public void testOnFilter() throws Exception {
        prepareMessage("happy");
        chatBot.onFilter(roomId, sender, "happy", messageId);
        String clientScript = getScriptInFirstMessageByRoomId(roomId);
        assertTrue(StringUtil.startsWith(clientScript, "client.character"));
        assertEquals(1, rsession.pathBy("/rooms/test/messages").childrenNames().size());

    }


    public void testHappy() throws Exception{
        prepareMessage("happy");
        chatBot.onFilter(roomId, sender, "happy", messageId);
        String clientScript = getScriptInFirstMessageByRoomId(roomId);
        assertTrue(clientScript.contains("motion(\"0\")"));

    }


    public void testAngry() throws Exception{
        prepareMessage("angry");
        chatBot.onFilter(roomId, sender, "angry", messageId);
        String clientScript = getScriptInFirstMessageByRoomId(roomId);
        assertTrue(clientScript.contains("motion(\"3\")"));

    }
    public void testSad() throws Exception{
        prepareMessage("sad");
        chatBot.onFilter(roomId, sender, "sad", messageId);
        String clientScript = getScriptInFirstMessageByRoomId(roomId);
        assertTrue(clientScript.contains("motion(\"1\")"));
    }


    private String getScriptInFirstMessageByRoomId(String roomId) {
        ReadNode messageNode = rsession.pathBy("/rooms/"+roomId+"/messages/").children().firstNode();
        return messageNode.property(Const.Message.ClientScript).stringValue();
    }


    private void prepareMessage(final String msg) throws Exception {
        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/messages/testMessage")
                        .property(Const.Message.Message, msg)
                        .property(Const.Message.ClientScript, "client.rooms().message(args.message);")
                        .property(Const.Message.Sender, "ryun")
                        .property(Const.Message.Event, Const.Event.onMessage);
                return null;
            }
        });
    }

}
