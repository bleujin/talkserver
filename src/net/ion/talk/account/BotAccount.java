package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.talk.ParameterMap;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.Message;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.script.BotMessage;
import net.ion.talk.script.BotScript;

public class BotAccount extends Account{

	private BotScript bs;
	private ReadSession session;
	
	protected BotAccount(BotScript bs, ReadSession session, String id) {
		super(id, Type.Bot);
		this.bs = bs ;
		this.session = session ;
	}

	@Override
	public void onMessage(String notifyId, TalkResponse response) {
		ReadNode notifyNode = session.pathBy("/notifies/" + accountId() + "/" + notifyId);
        ReadNode messageNode = notifyNode.ref(Const.Message.Message);
        String eventName = messageNode.property(Const.Message.Event).asString() ;

        BotMessage bm = BotMessage.create()
        			.botId(accountId())
        			.clientScript(Message.DefaultOnMessageClientScript)
        			.message(messageNode.property(Const.Message.Message).asString())
        			.roomId( messageNode.parent().parent().fqn().name())
        			.sender(messageNode.ref(Const.Message.Sender).property(Const.User.UserId).asString())
        			.messageId(messageNode.fqn().name()) ;
		bs.callFn(accountId() + "." + eventName, bm) ;
	}

}
