package net.ion.talk.script;

import net.ion.talk.TalkMessage;
import junit.framework.TestCase;

public class TestWhisperMessage extends TestCase {

	
	private TalkMessage tm;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'@echo Hello echo','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;
	}
	
	public void testCreate() throws Exception {
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
		this.tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'/time seoul','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;

		WhisperMessage wm = WhisperMessage.create(tm) ;
		assertEquals("system", wm.toUserId()) ;
		assertEquals("time seoul", wm.userMessage()) ;
		
		assertEquals("hero@i-on.net", wm.sender()) ;
		assertEquals("234", wm.fromRoomId()) ;
		assertEquals(2, wm.userMessages().length) ;
		assertEquals("time", wm.userMessages()[0]) ;
		assertEquals("seoul", wm.userMessages()[1]) ;
	}
	
	public void testAsCommand() throws Exception {
		this.tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'/time seoul','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}") ;

		WhisperMessage wm = WhisperMessage.create(tm) ;
		assertEquals("time", wm.asCommand().fnName()) ;
		assertEquals("seoul", wm.asCommand().remain(0)) ;
		
	}
	
	
	
}
