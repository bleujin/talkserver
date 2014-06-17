package net.ion.talk.account;

import org.infinispan.atomic.AtomicMap;

import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;

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
	public void onMessage(String notifyId, AtomicMap<PropertyId, PropertyValue> pmap) {
		appAccount.onMessage(notifyId, pmap);
		uconn.sendMessage(new JsonObject()
    			.put("notifyId", notifyId)
    			.put("result", new JsonObject().put(Const.Room.RoomId,   StringUtil.substringAfterLast(pmap.get(PropertyId.refer(Const.Room.RoomId)).asString(), "/"))
    							.put(Const.Message.MessageId,  notifyId)
       							.put(Const.Message.Sender,  pmap.get(PropertyId.normal(Const.Notify.SenderId)).asString())
    							.put(Const.Notify.SVGUrl, pmap.get(PropertyId.normal(Const.Notify.SVGUrl)).asString()))
					.toString());
	}

}
