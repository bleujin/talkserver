package net.ion.talk.account;

import net.ion.talk.UserConnection;

public interface AccountHandler {
	public Account create(AccountManager am, String userId, UserConnection uconn);
}