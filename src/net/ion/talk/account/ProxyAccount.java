package net.ion.talk.account;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.talk.UserConnection;

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
	public void onMessage(String notifyId) {
		appAccount.onMessage(notifyId);
		uconn.sendMessage(new JsonObject().put("notifyId", notifyId).toString());
	}

}
