package net.ion.talk.handler.engine;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.StringUtil;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.Connection;
import net.ion.talk.bean.Const.User;
import net.ion.talk.handler.TalkHandler;

public class UserConnectionHandler implements TalkHandler {

	private ReadSession rsession;

	@Override
	public Reason onConnected(TalkEngine tengine, final UserConnection uconn) {

		if (!uconn.isAllowUser(rsession.pathBy("/users/" + uconn.id()).property(User.AccessToken).stringValue())) {
			return Reason.NOTALLOW;
		}

		try {
			rsession.tranSync(new TransactionJob<Void>() {
				@Override
				public Void handle(WriteSession wsession) {
					String userId = uconn.id();
					wsession.pathBy("/connections/" + userId).property(Connection.DelegateServer, rsession.workspace().repository().memberId()).refTo("user", "/users/" + userId);
					WriteNode userNode = wsession.pathBy("/users/" + userId) ;
					userNode.unset(User.AccessToken);
					wsession.pathBy("/rooms/@" + userId + "/members/" + userId).refTo("user", "/users/" + userId);
					
					uconn.data("nick", StringUtil.defaultIfEmpty(userNode.property(Const.User.NickName).asString(), userId)) ;
					
					return null;
				}
			});
		} catch (Exception e) {
			return Reason.INTERNAL;
		}
		return Reason.OK;
	}

	@Override
	public void onClose(TalkEngine tengine, final UserConnection uconn) {
		try {
			rsession.tranSync(new TransactionJob<Void>() {
				@Override
				public Void handle(WriteSession wsession) throws Exception {
					wsession.pathBy("/connections/" + uconn.id()).removeSelf();
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
