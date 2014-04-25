package net.ion.talk.script;

import java.io.File;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.bouncycastle.asn1.tsp.TSTInfo;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.craken.node.crud.TestBaseCrud;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.aclient.Response.ResponseBuilder;
import net.ion.radon.core.let.MultiValueMap;
import net.ion.talk.ParameterMap;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import junit.framework.TestCase;

public class TestRoomScript extends TestBaseCrud {
	private ScheduledExecutorService ses;
	private TalkScript ts;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.ses = Executors.newScheduledThreadPool(2) ;
		this.ts = TalkScript.create(session, ses) ;
		ts.readDir(new File("./script")) ;
	}
	
	public void testCreateWith() throws Exception {
		
		MultiValueMap mvm = MultiValueMap.create(MapUtil.<Object>chainKeyMap().put("members", "bleujin").put("sender", "bleujin").put("roomId", "1234").toMap());
		ParameterMap params = ParameterMap.create(mvm);
		Object obj = ts.callFn("room/createWith", params) ;
		
		assertTrue(session.exists("/rooms/1234/members/bleujin"));
	}
	
	public void testBanWith() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/1234/members/bleujin") ;
				return null;
			}
		}) ;
		
		MultiValueMap mvm = MultiValueMap.create(MapUtil.<Object>chainKeyMap().put("members", "bleujin").put("sender", "bleujin").put("roomId", "1234").toMap());
		ParameterMap params = ParameterMap.create(mvm);
		Object obj = ts.callFn("room/banWith", params) ;
		
		assertFalse(session.exists("/rooms/1234/members/bleujin"));
	}
	
	public void testRef() throws Exception {
		testCreateWith(); 
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/bleujin").property("name", "bleujin").property("age", 20) ;
				return null;
			}
		}) ;

		ReadNode room = session.pathBy("/rooms/1234");
		ReadChildren members = room.child("members").children();
		
		String response = TalkResponseBuilder.create().newInner().property(room, "name").inlist("members").property(members, "user.name, user.age").build().toJsonElement().toString();
		
		Debug.line(response);
	}
	
	public void testInfoBy() throws Exception {
		testCreateWith(); 

		
		MultiValueMap mvm = MultiValueMap.create(MapUtil.<Object>chainKeyMap().put("roomId", "1234").toMap());
		ParameterMap params = ParameterMap.create(mvm);
		JsonObject result = JsonObject.fromString(ts.callFn("room/infoBy", params).toString()) ;
		
		assertEquals("bleujin", result.asJsonArray("members").get(0).getAsString());
	}
	
	public void testListNotifyDataBy() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/r01") ;
				wsession.pathBy("/rooms/r01/messages/msg01") ;
				wsession.pathBy("/notifies/bleujin/1234").refTo("roomId", "/rooms/r01").refTo("message", "/rooms/r01/messages/msg01") ;
				return null;
			}
		}) ;
		
		MultiValueMap mvm = MultiValueMap.create(MapUtil.<Object>chainKeyMap().put("userId", "bleujin").put("notifyId", "1234").toMap());
		ParameterMap params = ParameterMap.create(mvm);
		JsonObject result = JsonObject.fromString(ts.callFn("room/listNotifyDataBy", params).toString()) ;
		
		assertEquals("r01", result.asString("roomId"));
	}
	
	public void testListUnreadMessageBy() throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/rooms/1234/messages").property("roomId", "1234")
					.child("4").property("messageId", "4").property("message", "Hi bleujin").property("sender", "com");
				return null;
			}
		}) ;

		MultiValueMap mvm = MultiValueMap.create(MapUtil.<Object>chainKeyMap().put("roomId", "1234").put("messageId", "3").toMap());
		ParameterMap params = ParameterMap.create(mvm);
		JsonObject result = JsonObject.fromString(ts.callFn("room/listUnreadMessageBy", params).toString()) ;
		
		assertEquals("1234", result.asString("roomId"));
		assertEquals(1, result.asJsonArray("messages").size());
	}
	
	public void testRemoveNotifyDataWith() throws Exception {
		
		MultiValueMap mvm = MultiValueMap.create(MapUtil.<Object>chainKeyMap().put("userId", "bleujin").put("notifyId", "1234").toMap());
		ParameterMap params = ParameterMap.create(mvm);
		ts.callFn("room/removeNotifyDataWith", params) ;
	}
	
	public void testSendMessageWith() throws Exception {
		MultiValueMap mvm = MultiValueMap.create(MapUtil.<Object>chainKeyMap()
					.put("roomId", "1234")
					.put("sender", "bleujin").put("message", "Hi Bleujin").put("clientScript", ";").put("event", "onMessage").toMap());
		ParameterMap params = ParameterMap.create(mvm);
		ts.callFn("room/sendMessageWith", params) ;
		
		ReadNode node = session.pathBy("/rooms/1234/messages").children().firstNode() ;
		
		assertEquals("onMessage", node.property("event").asString()); 
	}
	
	
	
	
}
