package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;
import net.ion.talk.handler.engine.WhisperUserConnection;
import net.ion.talk.script.BotMessage;
import net.ion.talk.script.BotScript;

import org.infinispan.atomic.AtomicMap;

import com.google.common.cache.Cache;

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
	public void onMessage(String notifyId, EventMap pmap) {
        ReadNode notiNode = session.pathBy("/notifies/" + accountId() + "/" + notifyId);
        
        String senderId = notiNode.ref("message").ref("sender").property("userId").asString() ;
        UserConnection sender = tengine.findConnection(senderId) ;
        
        WhisperUserConnection conn = new WhisperUserConnection(tengine.context().getAttributeObject(Cache.class.getCanonicalName(), Cache.class), sender);
        
		bs.callFromOnMessage(BotMessage.create(conn, accountId(), notiNode)) ;
	}

}
