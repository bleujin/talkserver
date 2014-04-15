package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;
import net.ion.talk.account.AccountManager;
import net.ion.talk.bean.Const;
import net.ion.talk.handler.craken.BrokenMessageHandler;
import net.ion.talk.handler.craken.NotificationListener;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 15.
 * Time: 오후 1:24
 * To change this template use File | Settings | File Templates.
 */
public class TestBrokenMessageHandler extends TestCase {

    private ReadSession rsession;
    private TalkEngine tengine;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        tengine = TalkEngine.test();
        tengine.registerHandler(new BrokenMessageHandler());
        tengine.startForTest();

        rsession = tengine.readSession();
        rsession.workspace().addListener(new NotificationListener(new AccountManager(tengine, null)));

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/notifies/ryun").child("test").property(Const.Notify.CreatedAt, ToonServer.GMTTime());
                return null;
            }
        });
    }

    public void testBrokenMessage() throws Exception {

        BrokenMessageHandler.NOTIFY_LIFE_TIME = 2000;
        BrokenMessageHandler.NOTIFY_JOB_DELAY = 500;
        Thread.sleep(3000);
        long createdAt = rsession.pathBy("/notifies/ryun/test").property(Const.Notify.CreatedAt).longValue(0);
        assertTrue(ToonServer.GMTTime() - createdAt < 1500);

    }

    @Override
    public void tearDown() throws Exception {
        tengine.stopForTest();
        super.tearDown();
    }
}
