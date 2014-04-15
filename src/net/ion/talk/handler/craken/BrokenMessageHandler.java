package net.ion.talk.handler.craken;

import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import net.ion.craken.node.*;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.framework.util.Debug;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkMessage;
import net.ion.talk.ToonServer;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;
import net.ion.talk.handler.TalkHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 15.
 * Time: 오후 1:27
 * To change this template use File | Settings | File Templates.
 */
public class BrokenMessageHandler implements TalkHandler {

    public static int NOTIFY_LIFE_TIME = 30000;
    public static int NOTIFY_JOB_DELAY = 1000;
    private ReadSession rsession;

    private ScheduledExecutorService es = Executors.newScheduledThreadPool(5);

    @Override
    public TalkEngine.Reason onConnected(TalkEngine tengine, UserConnection uconn) {
        return TalkEngine.Reason.OK;
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws Exception {
        rsession = tengine.readSession();
        es.schedule(new NotifyDoctorJob(), NOTIFY_JOB_DELAY, TimeUnit.MILLISECONDS );

    }


    private void revivalNotifies(final ReadChildren deadNotifies) throws Exception {

        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                for(ReadNode deadNotify : deadNotifies){
                    wsession.pathBy(deadNotify.fqn()).property(Const.Notify.CreatedAt, ToonServer.GMTTime());
                }
                return null;
            }
        });
    }

    private ReadChildren getDeadNotifiesByUser(ReadNode iter) {
        return iter.children().filter(new Predicate<ReadNode>() {
            @Override
            public boolean apply(@Nullable ReadNode notify) {
                return (ToonServer.GMTTime() - notify.property(Const.Notify.CreatedAt).longValue(0) > NOTIFY_LIFE_TIME);
            }
        });
    }


    @Override
    public void onEngineStop(TalkEngine tengine) {
        es.shutdown();
    }

    class NotifyDoctorJob implements Runnable{

        @Override
        public void run() {

            for(ReadNode userInNotify : rsession.pathBy("/notifies").children()){

                ReadChildren deadNotifies = getDeadNotifiesByUser(userInNotify);
                try {
                    revivalNotifies(deadNotifies);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            es.schedule(this, NOTIFY_JOB_DELAY, TimeUnit.MILLISECONDS);

        }
    }

}
