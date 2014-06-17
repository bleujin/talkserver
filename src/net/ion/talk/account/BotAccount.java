package net.ion.talk.account;

import org.infinispan.atomic.AtomicMap;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.script.BotMessage;
import net.ion.talk.script.BotScript;

public class BotAccount extends Account{

	private BotScript bs;
	private ReadSession session;
	private TalkEngine tengine;
	
	protected BotAccount(TalkEngine tengine, BotScript bs, ReadSession session, String id) {
		super(id, Type.BOT);
		this.tengine = tengine ;
		this.bs = bs ;
		this.session = session ;
	}

	@Override
	public void onMessage(String notifyId, AtomicMap<PropertyId, PropertyValue> pmap) {
        ReadNode notiNode = session.pathBy("/notifies/" + accountId() + "/" + notifyId);
        
        String senderId = notiNode.ref("message").ref("sender").property("userId").asString() ;
        UserConnection sender = tengine.findConnection(senderId) ;
        
		bs.callFromOnMessage(BotMessage.create(sender, accountId(), notiNode)) ;
	}

}
