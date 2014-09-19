package net.ion.talk.account;

import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;

import org.infinispan.atomic.AtomicMap;

public class ProxyAccount extends Account{

	private UserConnection uconn;
	private Account appAccount;

	public ProxyAccount(AccountManager am, String userId, UserConnection uconn, Account appAccount) {
		super(userId, Type.PROXY) ;
		this.uconn = uconn ;
		this.appAccount = appAccount ;
	}

	public final static ProxyAccount create(AccountManager am, String userId, UserConnection uconn, Account appAccount){
		return new ProxyAccount(am, userId, uconn, appAccount) ;
	}

	@Override
	public void onMessage(String notifyId, EventMap pmap) {
		appAccount.onMessage(notifyId, pmap);
		uconn.sendMessage(new JsonObject()
    			.put("notifyId", notifyId)
    			.put("result", new JsonObject().put(Const.Room.RoomId,   StringUtil.substringAfterLast(pmap.refer(Const.Room.RoomId).asString(), "/"))
    							.put(Const.Message.MessageId,  notifyId)
       							.put(Const.Message.Sender,  pmap.property(Const.Notify.SenderId).asString())
    							.put(Const.Notify.SVGUrl, pmap.property(Const.Notify.SVGUrl).asString()))
					.toString());
	}

}
