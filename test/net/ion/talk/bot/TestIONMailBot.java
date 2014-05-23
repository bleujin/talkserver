package net.ion.talk.bot;

import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.talk.TalkMessage;
import net.ion.talk.fake.FakeUserConnection;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.WhisperMessage;

public class TestIONMailBot extends TestBaseServer {

	
	
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

	public void testHelp() throws Exception {
		JsonObject result = callSystemWhisper("@ionmail help");
		
//		Debug.line(source.receivedMessage()) ;
		assertEquals("success", result.asString("status"));
		assertEquals("/whisper/ionmail/help", result.asString("script"));
		assertEquals("ionmail", result.asJsonObject("result").asString("sender"));
		assertEquals("{event:'onWhisper'}", result.asJsonObject("result").asString("options"));
	}
	


	
	public void testPassword() throws Exception {
		JsonObject result = callSystemWhisper("@ionmail password bleujin7");
		
//		Debug.line(source.receivedMessage()) ;
		assertEquals("success", result.asString("status"));
		assertEquals("/whisper/ionmail/password", result.asString("script"));
		assertEquals("ionmail", result.asJsonObject("result").asString("sender"));
		assertEquals("{event:'onWhisper'}", result.asJsonObject("result").asString("options"));
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
