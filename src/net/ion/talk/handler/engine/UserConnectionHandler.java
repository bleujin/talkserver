package net.ion.talk.handler.engine;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.handler.TalkHandler;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const.User;
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
    public Reason onConnected(TalkEngine tengine, final UserConnection uconn) {

        if (! uconn.isAllowUser(rsession.pathBy("/users/" + uconn.id()).property(User.AccessToken).stringValue())){
        	return Reason.NOTALLOW;
        }

        try {
			rsession.tranSync(new TransactionJob<Void>() {
			    @Override
			    public Void handle(WriteSession wsession) {
			        wsession.pathBy("/connections/"+uconn.id())
			                .refTo("user","/users/"+uconn.id());
			        wsession.pathBy("/connections/"+uconn.id()).property(User.DelegateServer, rsession.workspace().repository().memberId());
                    wsession.pathBy("/users/"+uconn.id()).unset(User.AccessToken);
			        return null;
			    }
			});
		} catch (Exception e) {
			return Reason.INTERNAL ;
		}
        return Reason.OK ;
    }

    @Override
    public void onClose(TalkEngine tengine, final UserConnection uconn) {
        try {
            rsession.tranSync(new TransactionJob<Void>() {
                @Override
                public Void handle(WriteSession wsession) throws Exception {
                    wsession.pathBy("/connections/"+uconn.id()).removeSelf();
                    return null;
                }
            });
        } catch (Exception e) {
        	tengine.getLogger().warning(e.getLocalizedMessage());
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
