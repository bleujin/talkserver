package net.ion.talk.engine;

import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.radon.util.AradonTester;
import net.ion.talk.account.Account;
import net.ion.talk.account.AccountManager;
import net.ion.talk.account.Bot;
import net.ion.talk.account.ConnectedUserAccount;
import net.ion.talk.account.DisconnectedAccount;
import net.ion.talk.account.Account.Type;
import net.ion.talk.bean.Const;
import net.ion.talk.bot.TestCrakenBase;
import net.ion.talk.fake.FakeSender;
import net.ion.talk.fake.FakeTalkEngine;
import net.ion.talk.fake.FakeUserConnection;
import net.ion.talk.responsebuilder.TalkResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 16.
 * Time: 오전 10:30
 * To change this template use File | Settings | File Templates.
 */
public class TestAccountManager extends TestCrakenBase{

    private AccountManager am;
    private FakeTalkEngine fakeEngine;
    private FakeSender sender;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fakeEngine = new FakeTalkEngine(rentry);
        sender = new FakeSender();
        am = AccountManager.create(fakeEngine, sender);
    }

    public void testIsConnectedUser() throws Exception {
        String user = "ryun";
        fakeEngine.addConnectedUser(user);
        Account account = am.newAccount(user);
        assertTrue(account instanceof ConnectedUserAccount);
    }

    public void testIsDisconnectedUser(){
        String user = "ryun";
        createUserToCraken(user);
        Account account = am.newAccount(user);
        assertTrue(account instanceof DisconnectedAccount);
    }

    public void testIsNotFoundUser(){
        String user = "ryun";
        Account account = am.newAccount(user);
        assertEquals(Account.Type.NotFoundUser, account.type());
    }

    public void testIsBot() throws Exception {
        String bot = "echoBot";
        createBotToCraken(bot, "http://localhost:9000", false);
        Account account = am.newAccount(bot);
        assertTrue(account instanceof Bot);
    }

    public void testSendToConnectedUser() throws Exception {
        String user = "ryun";
        String notifyId = "1234";

        createUserToCraken(user);
        FakeUserConnection uconn = fakeEngine.addConnectedUser(user);

        Account account = am.newAccount(user);
        TalkResponse notifyMsg = createNotify(user, notifyId);
        account.onMessage(notifyId, notifyMsg);

        assertEquals(notifyMsg.toString(), uconn.receivedMessage());

    }

    public void testSendToDisconnectedUser() throws Exception {
        String user = "ryun";
        String notifyId = "1234";
        createUserToCraken(user);

        Account account = am.newAccount(user);
        TalkResponse notifyMsg = createNotify(user, notifyId);
        account.onMessage(notifyId, notifyMsg);

        assertEquals(notifyMsg.toString(), sender.getMessage());

    }

    public void xtestSendToBot() throws Exception {
        String bot = "echoBot";
        String notifyId = "1234";
        createBotToCraken(bot, "http://localhost:9000", false);

        Account account = am.newAccount(bot);

        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/notifies/echoBot/1234").refTo(Const.Message.Message, "/rooms/test/testMessage");
                wsession.pathBy("/rooms/test/testMessage").property(Const.Message.MessageId, "testMessage")
                        .property(Const.Message.Message, "Hello");
                return null;
            }
        });

        TalkResponse notifyMsg = createNotify(bot, notifyId);
        account.onMessage(notifyId, notifyMsg);

    }


}
