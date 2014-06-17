package net.ion.talk.handler.engine;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.mte.Engine;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.Response.ResponseBuilder;
import net.ion.talk.fake.FakeUserConnection;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import junit.framework.TestCase;

public class TestWhisperHandler extends TestCase {

	
	public void testPrepareMessage() throws Exception {
		TalkResponseBuilder rb = TalkResponseBuilder.create() ;
		String tm = rb.makeCommandBuilder("/whisper/system/time")
		  .inner("result")
		  	.property("sender", "system")
			.property("clientScript", "client.room().message(args);")
			.property("message", "Hello World")
			.property("time", new Date().getTime())
			.property("options", "{event:'onWhisper'}")
			.property("messageId", new net.ion.framework.util.ObjectId().toString()).build().talkMessage() ;
		
		Map<String, Object> map = JsonObject.fromString(tm).toMap() ;
		
		for(Entry<String, Object> entry : map.entrySet()){
			Debug.line(entry.getKey(), entry.getValue().getClass(), entry.getValue());
		}
		
		
	}
	
}
