package net.ion.talk.bot;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.talk.TalkMessage;
import net.ion.talk.fake.FakeUserConnection;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.WhisperMessage;

public class TestSystemBot extends TestBaseServer {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		talkEngine.readSession().tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("rooms/roomroom/members/bleujin@i-on.net") ;
				return null;
			}
		}) ;
		
	}
	
	public void testTime() throws Exception {
		JsonObject result = callSystemWhisper("/time");
		
//		Debug.line(source.receivedMessage()) ;
		assertEquals("success", result.asString("status"));
		assertEquals("/whisper/system/time", result.asString("script"));
		assertEquals("system", result.asJsonObject("result").asString("sender"));
		assertEquals("{event:'onWhisper'}", result.asJsonObject("result").asString("options"));
	}

	public void testWhoami() throws Exception {
		JsonObject json = callSystemWhisper("/whoami");
		Debug.line(json) ;
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/whoami", json.asString("script"));
	}

	public void testRooms() throws Exception {
		JsonObject json = callSystemWhisper("/rooms");
		Debug.line(json) ;
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/rooms", json.asString("script"));
	}

	
	public void testMembers() throws Exception {
		JsonObject json = callSystemWhisper("/members");
		Debug.line(json) ;
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/members", json.asString("script"));
	}
	
	public void testInfo() throws Exception {
		JsonObject json = callSystemWhisper("/info");
		Debug.line(json) ;
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/info", json.asString("script"));
	}
	

	public void testBot() throws Exception {
		JsonObject json = callSystemWhisper("/bot system");
		Debug.line(json) ;
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/bot", json.asString("script"));
	}
	

	public void testBan() throws Exception {
		JsonObject json = callSystemWhisper("/ban bleujin@i-on.net");
		Debug.line(json) ;
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/ban", json.asString("script"));
	}
	
	public void testTopic() throws Exception {
		JsonObject json = callSystemWhisper("/topic HelloWorld");
		Debug.line(json) ;
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/topic", json.asString("script"));
	}
	
	public void testInvite() throws Exception {
		JsonObject json = callSystemWhisper("/invite hero@i-on.net");
		Debug.line(json) ;
		
		ReadSession session = talkEngine.readSession() ;
		assertEquals(true, session.pathBy("/rooms/@hero@i-on.net/messages").children().toList().size() == 1); // sended invite-message
		
		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/invite", json.asString("script"));
	}


	public void testLeave() throws Exception {
		JsonObject json = callSystemWhisper("/leave");
		Debug.line(json) ;

		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/leave", json.asString("script"));

	}
	

	public void testJoin() throws Exception {
		JsonObject json = callSystemWhisper("/join 222");
		Debug.line(json) ;

		ReadSession session = talkEngine.readSession() ;
		assertEquals(true, session.pathBy("/rooms/222/messages").children().toList().size() == 1); // entered bleujin

		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/join", json.asString("script"));

	}
	
	public void testWhisperUnreadList() throws Exception {
		ReadSession session = talkEngine.readSession() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/@bleujin@i-on.net/messages/123")
					.property("messageId", "123")
					.property("message", "Hello").refTo("sender", "/users/system") ;
				return null;
			}
		}) ;
		
		JsonObject json = callSystemWhisper("/whisper");
		Debug.line(json) ;

		assertEquals("success", json.asString("status"));
		assertEquals("/whisper/system/whisper", json.asString("script"));

		
		json = callSystemWhisper("/whisper 2");
		Debug.line(json) ;
	}
	

	private JsonObject callSystemWhisper(String message) {
		BotScript bs = talkEngine.context().getAttributeObject(BotScript.class.getCanonicalName(), BotScript.class) ;
		
		JsonObject paramJson = JsonObject.fromString("{receivers='', roomId='roomroom', message:'" + message  + "', sender:'bleujin@i-on.net', senderNickname:'self', clientScript:'client.room().message(args)', requestId:'1234'}");
		TalkMessage tm = TalkMessage.fromJsonString(new JsonObject().put("id", "1234").put("script", "/room/sendMessage").put("params", paramJson).toString()) ;

		FakeUserConnection source = FakeUserConnection.fake("bleujin@i-on.net") ;
		WhisperMessage wm = WhisperMessage.create(source, tm) ;
		bs.whisper(source, wm) ;

		return JsonObject.fromString(source.receivedMessage());
	}
	

}
