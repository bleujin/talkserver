package net.ion.talk.responsebuilder;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import net.ion.craken.node.ReadNode;
import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestCommandBuilder extends TestCase {

	public void testCommandBuilder() throws Exception {
		String roomId = "1234" ;
		String result = TalkResponseBuilder.makeCommandBuilder("/commands/join")
			.inner("result")
			.property("clientScript", "client.room().join('" + roomId + "');")
			.build().talkMessage() ;
		
		Debug.line(result);
		
		Iterable<ReadNode> unreadMessages = new Iterable<ReadNode>(){
			@Override
			public Iterator<ReadNode> iterator() {
				return Iterators.emptyIterator();
			}
		};
		
		result = TalkResponseBuilder.makeCommandBuilder("/commands/join").property("result", 
				TalkResponseBuilder.create().newInner().inlist("messages", unreadMessages,"message,sender.userId as sender,sender.nickname as senderNickname,requestId, clientScript,event, messageId,time").property("roomId", roomId).build().toJsonElement()).build().talkMessage() ;
		Debug.line(result);
	}
}
