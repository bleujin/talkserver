package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;


public class TestWebSocketTalkMessageHandler extends TestCase {

	private TalkEngine tengine;
	private ReadSession rsession;
	private FakeWebSocketConnection ryun;

	public void setUp() throws Exception {

		tengine = TalkEngine.testCreate().init().clearHandler().registerHandler(new TalkScriptHandler()).startEngine();
		rsession = tengine.readSession();
		ryun = FakeWebSocketConnection.create("ryun");

		rsession.tranSync(new TransactionJob<Object>() {

			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/ryun").property("accessToken", "testToken");

				wsession.pathBy("/script/users/register").property(
						"script",
						"session.tranSync(function(wsession){\n" + "  wsession.pathBy(\"/users/\" + params.asString(\"userId\"))\n" +
						"    .property(\"phone\",params.asString(\"phone\"))\n" + 
						"    .property(\"nickname\",params.asString(\"nickname\"))\n" + 
						"    .property(\"pushId\",params.asString(\"pushId\"))\n" +
						"    .property(\"deviceOS\",params.asString(\"deviceOS\"))\n" +
						"    .property(\"friends\", \"\");\n" + "});");

				wsession.pathBy("/script/users/info").property("script", "var user=session.pathBy(\"/users/\"+params.asString(\"userId\")); rb.create().newInner().property(user,\"nickname, phone\").build().toJsonObject();");
				return null;
			}
		});

		ryun.data("accessToken", "testToken");
	}
	

	@Override
	public void tearDown() throws Exception {
		tengine.stopEngine();
		super.tearDown();
	}

	public void xtestSuceessSendMessage() throws Exception {
		tengine.onOpen(ryun);
		tengine.onMessage(ryun, "{\"script\":\"/user/registerWith\", \"id\":\"userRegister\",\"params\":{\"userId\":\"ryun\", \"phone\":\"0101234568\",\"nickname\":\"ryuneeee\",\"pushId\":\"lolem ipsum pushId\",\"deviceOS\":\"android\",\"friends\":[\"alex\",\"lucy\"]}}");
		JsonObject jso = JsonObject.fromString(ryun.recentMsg()) ;
		
		assertEquals("userRegister", jso.asString("id"));
		assertEquals("success", jso.asString("status"));
		assertEquals("undefined", jso.asString("result"));
		assertEquals("/user/registerWith", jso.asString("script"));
	}

	public void testInvalidScriptName() throws Exception {
		tengine.onOpen(ryun);

		tengine.onMessage(ryun, "{'id':1234, 'script':'/users/info'}");
		Debug.line(ryun.recentMsg());
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));

	}

	public void testEmptyMessage() {
		tengine.onOpen(ryun);
		tengine.onMessage(ryun, "{}");
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
		tengine.onMessage(ryun, "''");
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
	}

	public void testInvalidScriptWillBeEchoed() {
		tengine.onOpen(ryun);
		tengine.onMessage(ryun, "{k:0}");
		Debug.line(ryun.recentMsg());
		assertEquals("failure", JsonObject.fromString(ryun.recentMsg()).asString("status"));
//		tengine.onMessage(ryun, "{안녕}");
//		assertEquals("illegal message : {안녕}", ryun.recentMsg());
//		tengine.onMessage(ryun, "{\"cript\":\"hell\"}");
//		assertEquals("illegal message : {\"cript\":\"hell\"}", ryun.recentMsg());
	}

}
