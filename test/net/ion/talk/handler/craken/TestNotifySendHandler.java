package net.ion.talk.handler.craken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import sun.nio.cs.MS1250;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.message.push.sender.PushMessage;
import net.ion.message.push.sender.PushResponse;
import net.ion.message.push.sender.Sender;
import net.ion.message.push.sender.SenderConfig;
import net.ion.message.push.sender.strategy.PushStrategy;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.util.AradonTester;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;
import net.ion.talk.ToonServer;
import net.ion.talk.UserConnection;
import net.ion.talk.responsebuilder.TalkResponse;
import junit.framework.TestCase;

public class TestNotifySendHandler extends TestCase {

	public void testFirst() throws Exception {
		RepositoryEntry rentry = RepositoryEntry.test();
		ReadSession rsession = rentry.login();

		FakeSender sender = new FakeSender();
		FakeTalkEngine tengine = new FakeTalkEngine();
		NotificationListener handler = new NotificationListener(tengine, sender);
		rsession.workspace().addListener(handler) ;
		final String memberId = rsession.workspace().repository().memberId() ;

		rsession.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				String notifyID = "1234";
				String userId = "bleujin";
				String roomId = "roomId";
				String messageId = "messageId";
				
				wsession.pathBy("/notifies/" + userId)
					.property("lastNotifyId", notifyID).addChild(notifyID)
					.property("delegateServer", memberId).property("createdAt", ToonServer.GMTTime()).refTo("message", "/rooms/" + roomId + "/messages/" + messageId).refTo("roomId", "/rooms/" + roomId);
				return null;
			}
		});

		String pushReceived = sender.sendeds.get("bleujin").received() ;
		
		Debug.line(pushReceived); 
//		
//		FakeUserConnection bleujin = tengine.getUserConnection("bleujin") ;
//		String msg = bleujin.receivedMessage() ;
//		Debug.line(msg); 
	}
}


class FakeTalkEngine extends TalkEngine{
	
	public Map<String, FakeUserConnection> users = MapUtil.newMap() ;
	public FakeTalkEngine() throws Exception{
		super(AradonTester.create().getAradon()) ;
	}
	
	public FakeUserConnection findConnection(String userId){
		return users.get(userId) ;
	}
	
	@Override
	public void sendMessage(String userId, Sender sender, TalkResponse tresponse){
		super.sendMessage(userId, sender, tresponse);
	}
	
	
}

class FakeUserConnection extends UserConnection {
	private String received;
	protected FakeUserConnection(WebSocketConnection inner) {
		super(inner);
	}

	public String receivedMessage() {
		return received;
	}

	public void sendMessage(String message) {
		this.received = message ;
	}
}

class FakeSender extends Sender{

	public Map<String, FakePushMessage> sendeds = MapUtil.newMap() ;
	protected FakeSender() {
		super(null, null, null);
	}
	
	@Override
	public FakePushMessage sendTo(String... receiver) {
		FakePushMessage result = new FakePushMessage(this, receiver) ;
		sendeds.put(StringUtil.join(receiver), result) ;
		return result ;
	}
	
}

class FakePushMessage extends PushMessage{
	private String received;

	public FakePushMessage(Sender sender, String[] receivers) {
		super(sender, receivers);
	}
	
	@Override
	public Future<List<PushResponse>> sendAsync(String message) {
		this.received = message ;
		return null ;
	}
	
	public String received(){
		return received ;
	}
	
}

