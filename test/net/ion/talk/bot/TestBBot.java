package net.ion.talk.bot;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.TestCrakenBase;
import net.ion.talk.bean.Const;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 25.
 * Time: 오전 11:34
 * To change this template use File | Settings | File Templates.
 */
public class TestBBot extends TestCrakenBase{

    private BBot bBot;
    private String roomId = "test";
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        bBot = new BBot(rsession, ses);

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/"+roomId+"/members/"+bBot.id());
                return null;
            }
        });
    }

    public void testOnEnter() throws Exception {
        bBot.onEnter(roomId, bBot.id());
        watingJobOnShutdown();
        assertEquals("안녕하세요. B@bot 입니다! 도움이 필요하다면 \"B@메시지\"이라고 입력해주세요 :) ex) B@도움말, B@식당, B@월차", getFirstMsgByRoomId(roomId));
    }

    public void testOnExit() throws Exception {
        bBot.onExit(roomId, bBot.id());
        watingJobOnShutdown();
        assertEquals("나중에 또 봐요 ~", getFirstMsgByRoomId(roomId));

    }

    public void testHelpWithOutAccount() throws Exception {
        bBot.onMessage(roomId, "ryun", "B@도움말");
        watingJobOnShutdown();
        assertEquals("계정 정보가 없습니다. \"/register 이메일 비밀번호\"를 이용하여 계정정보를 입력해주세요.", getFirstMsgByRoomId(roomId));
    }

    public void testRegisterAccount() throws Exception {
        bBot.onMessage(roomId, "ryun", "/register ryun@i-on.net ryun");
        watingJobOnShutdown();
        assertEquals("ryun@i-on.net 계정이 정상적으로 등록되었습니다!", getFirstMsgByRoomId(roomId));
    }

    public void testHelpWithAccount() throws Exception {
        bBot.onMessage(roomId, "ryun", "/register ryun@i-on.net ryun");
        Thread.sleep(500);
        assertEquals("ryun@i-on.net 계정이 정상적으로 등록되었습니다!", getFirstMsgByRoomId(roomId));
        removeRecentMessage();

        bBot.onMessage(roomId, "ryun", "B@도움말");
        watingJobOnShutdown();
        assertEquals("명령을 보냈습니다!", getFirstMsgByRoomId(roomId));
        removeRecentMessage();
    }

    private String getFirstMsgByRoomId(String roomId) {
        ReadNode messageNode = rsession.pathBy("/rooms/"+roomId+"/messages/").children().firstNode();
        return messageNode.property(Const.Message.Message).stringValue();
    }

    private void removeRecentMessage() throws Exception {
        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/test/messages/").children().firstNode().removeSelf();
                return null;
            }
        });
    }

    //Echobot이 가지고 있는 ScheduledExecutor이 job을 끝내는 시간동안 기다림
    private void watingJobOnShutdown() throws InterruptedException {
        ses.shutdown();
        ses.awaitTermination(2, TimeUnit.SECONDS);

    }
}
