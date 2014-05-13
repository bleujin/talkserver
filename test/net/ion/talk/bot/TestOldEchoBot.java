package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.account.Bot;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 25. Time: 오전 11:34 To change this template use File | Settings | File Templates.
 */
public class TestOldEchoBot extends TestCrakenBase{

	private EchoBot echoBot;
    private ScheduledExecutorService ses;
    private String roomId = "testRoom";

    @Override
	public void setUp() throws Exception {
		super.setUp();
        ses = Executors.newScheduledThreadPool(2);
		echoBot = new EchoBot(rsession, ses);
		rsession.tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/"+roomId+"/members/"+echoBot.id);
				return null;
			}
		});
	}

    public void testOnEnter() throws Exception {
		echoBot.onEnter(roomId, "ryun");
        WatingJobOnShutdown();
		assertEquals("Hello! ryun", getFirstMsgByRoomId(roomId));
	}

    public void testOnExit() throws Exception {
		echoBot.onExit(roomId, "ryun");
        WatingJobOnShutdown();
		assertEquals("Bye! ryun", getFirstMsgByRoomId(roomId));

	}

    public void testOnSelfEnter() throws Exception {
        echoBot.onEnter(roomId, echoBot.id());
        WatingJobOnShutdown();
        assertEquals("Hello I'm EchoBot. please type: /help", getFirstMsgByRoomId(roomId));
    }

    public void testOnSelfExit() throws Exception {
        echoBot.onExit(roomId, echoBot.id());
        WatingJobOnShutdown();
        assertEquals("Bye~ see you later!", getFirstMsgByRoomId(roomId));
    }

    public void testOnMessage() throws Exception {
		echoBot.onMessage(roomId, "ryun", "Everybody Hello!");
        WatingJobOnShutdown();
		assertEquals("Everybody Hello!", getFirstMsgByRoomId(roomId));
	}

    public void testOnDelayedMessage() throws Exception {
        echoBot.onMessage(roomId, "ryun", "/delay 1");
        Thread.sleep(500);
        assertEquals("ryun 사용자에게는 봇이 1초 후에 반응합니다.", getFirstMsgByRoomId(roomId));

        clearMessagesInRoom(roomId);
        echoBot.onMessage(roomId, "ryun", "Hello");
        Thread.sleep(1500);
        assertEquals("Hello", getFirstMsgByRoomId(roomId));
        WatingJobOnShutdown();

    }

    //Echobot이 가지고 있는 ScheduledExecutor이 job을 끝내는 시간동안 기다림
    private void WatingJobOnShutdown() throws InterruptedException {
        ses.shutdown();
        ses.awaitTermination(2, TimeUnit.SECONDS);
    }

    private String getFirstMsgByRoomId(String roomId) {
        ReadNode messageNode = rsession.pathBy("/rooms/"+roomId+"/messages/").children().firstNode();
        return messageNode.property(Const.Message.Message).stringValue();
    }

    private void clearMessagesInRoom(final String roomId){
        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/"+roomId+"/messages").removeSelf();
                return null;
            }
        });
    }

}
