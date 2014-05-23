package net.ion.talk.script;

import java.util.Date;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.talk.TalkMessage;
import net.ion.talk.TalkMessage.MType;

public class TestTalkMessage extends TestCase {

	public void testMakeNormal() throws Exception {
		String id = String.valueOf(new Date().getTime());
		
		JsonObject paramJson = JsonObject.fromString("{receivers='', roomId='roomroom', message:'Hello', sender:'bleujin', senderNickname:'self', clientScript:'client.room().message(args)', requestId:'" + id +"'}");
		TalkMessage tm = TalkMessage.fromJsonString(new JsonObject().put("id", id).put("script", "/room/sendMessage").put("params", paramJson).toString()) ;
		
		assertEquals("/room/sendMessage", tm.scriptPath()) ;
		assertEquals("roomroom", tm.params().asString("roomId")) ;
		assertEquals(MType.NORMAL, tm.messageType()) ;
	}


	public void testWhisper() throws Exception {
		String id = String.valueOf(new Date().getTime());
		
		JsonObject paramJson = JsonObject.fromString("{receivers='', roomId='roomroom', message:'@system time', sender:'bleujin', senderNickname:'self', clientScript:'client.room().message(args)', requestId:'" + id +"'}");
		TalkMessage tm = TalkMessage.fromJsonString(new JsonObject().put("id", id).put("script", "/room/sendMessage").put("params", paramJson).toString()) ;
		
		assertEquals("/room/sendMessage", tm.scriptPath()) ;
		assertEquals("roomroom", tm.params().asString("roomId")) ;
		assertEquals(MType.WHISPER, tm.messageType()) ;
	}

	public void testCommandToSystemWhisper() throws Exception {
		String id = String.valueOf(new Date().getTime());
		
		JsonObject paramJson = JsonObject.fromString("{receivers='', roomId='roomroom', message:'/time', sender:'bleujin', senderNickname:'self', clientScript:'client.room().message(args)', requestId:'" + id +"'}");
		TalkMessage tm = TalkMessage.fromJsonString(new JsonObject().put("id", id).put("script", "/room/sendMessage").put("params", paramJson).toString()) ;
		
		assertEquals("/room/sendMessage", tm.scriptPath()) ;
		assertEquals("roomroom", tm.params().asString("roomId")) ;
		assertEquals(MType.WHISPER, tm.messageType()) ;
	}


	

}
