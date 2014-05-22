package net.ion.talk.script;

import net.ion.talk.TalkMessage;
import junit.framework.TestCase;

public class TestWhisperMessage extends TestCase {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testCreate() throws Exception {
		TalkMessage  tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'@echo Hello echo','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;
		WhisperMessage wm = WhisperMessage.create(tm) ;
		
		assertEquals("echo", wm.toUserId()) ;
		assertEquals("Hello echo", wm.userMessage()) ;
		
		assertEquals("hero@i-on.net", wm.sender()) ;
		assertEquals("234", wm.fromRoomId()) ;
		assertEquals(2, wm.userMessages().length) ;
		assertEquals("Hello", wm.userMessages()[0]) ;
		assertEquals("echo", wm.userMessages()[1]) ;
	}
	
	public void testCommandToSystemWhisper() throws Exception {
		TalkMessage  tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'/time seoul','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;

		WhisperMessage wm = WhisperMessage.create(tm) ;
		assertEquals("system", wm.toUserId()) ;
		assertEquals("/time seoul", wm.userMessage()) ;
		
		assertEquals("hero@i-on.net", wm.sender()) ;
		assertEquals("234", wm.fromRoomId()) ;
		assertEquals(2, wm.userMessages().length) ;
		assertEquals("/time", wm.userMessages()[0]) ;
		assertEquals("seoul", wm.userMessages()[1]) ;
	}
	
	public void testAsCommand() throws Exception {
		TalkMessage tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'/time seoul','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;

		WhisperMessage wm = WhisperMessage.create(tm) ;
		assertEquals("time", wm.asCommand().fnName()) ;
		assertEquals("seoul", wm.asCommand().remain(0)) ;
		assertEquals("", wm.asCommand().remain(1)) ;
		assertEquals(100, wm.asCommand().remainAsInt(1, 100)) ;
	}
	

	public void testAsCommand2() throws Exception {
		TalkMessage  tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'@echo /time seoul','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;

		WhisperMessage wm = WhisperMessage.create(tm) ;
		assertEquals("time", wm.asCommand().fnName()) ;
		assertEquals("seoul", wm.asCommand().remain(0)) ;
		assertEquals("", wm.asCommand().remain(1)) ;
		assertEquals(100, wm.asCommand().remainAsInt(1, 100)) ;
	}
	
public void testWhipser() throws Exception {
		TalkMessage newm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'/whisper','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;

		WhisperMessage nwm = WhisperMessage.create(newm) ;
		assertEquals("", nwm.asCommand().remain(0)) ;
		
	}
	
	
	
}
