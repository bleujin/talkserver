package net.ion.talk.account;

import net.ion.craken.node.ReadSession;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.script.BotMessage;
import net.ion.talk.script.BotScript;

public class BotAccount extends Account{

	private BotScript bs;
	private ReadSession session;
	
	protected BotAccount(BotScript bs, ReadSession session, String id) {
		super(id, Type.BOT);
		this.bs = bs ;
		this.session = session ;
	}

	@Override
	public void onMessage(String notifyId) {
        bs.callFromOnMessage(BotMessage.create(accountId(), session.pathBy("/notifies/" + accountId() + "/" + notifyId))) ;
	}

}
