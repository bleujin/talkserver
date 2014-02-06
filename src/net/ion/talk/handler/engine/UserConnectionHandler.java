package net.ion.talk.handler.engine;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkHandler;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 3:36
 * To change this template use File | Settings | File Templates.
 */
public class UserConnectionHandler implements TalkHandler {

    private ReadSession rsession;

    @Override
    public void onConnected(TalkEngine tengine, final UserConnection uconn) {
        try {
            rsession.tranSync(new TransactionJob<Void>() {
                @Override
                public Void handle(WriteSession wsession) throws Exception {
                    wsession.pathBy("/users/"+uconn.id()+"/connection").property("isConnected", true).property("server", rsession.workspace().repository().memberId());
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(TalkEngine tengine, final UserConnection uconn) {
        try {
            rsession.tranSync(new TransactionJob<Void>() {
                @Override
                public Void handle(WriteSession wsession) throws Exception {
                    wsession.pathBy("/users/"+uconn.id()+"/connection").property("isConnected", false).property("server", "none");
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws IOException {
        this.rsession = tengine.readSession();
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }
}
