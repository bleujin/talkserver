package net.ion.talk.responsebuilder;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.TestBaseCrud;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

public class TestResponseWrapper extends TestBaseCrud {

	private TalkResponseBuilder newBuilder;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.newBuilder = TalkResponseBuilder.create();
	}

	public void testFlat() throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				WriteNode bleujin = wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20);
				return null;
			}
		});

		ReadNode bleujin = session.pathBy("/bleujin");
		TalkResponse response = newBuilder.newInner().property(bleujin, "name, age").property("nick", "mine").build();
		// {"nick":"mine","age":20,"name":"bleujin"}
		assertEquals("mine", response.toJsonObject().asString("nick"));
		assertEquals("bleujin", response.toJsonObject().asString("name"));
		assertEquals(20, response.toJsonObject().asInt("age"));
	}

	public void testArray() throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/bleujin").append("name", "bleu", "jin", "hero");
				return null;
			}
		});

		ReadNode bleujin = session.pathBy("/bleujin");
		TalkResponse response = newBuilder.newInner().property(bleujin, "[name] as name, age").build();
		// {name:[bleu, jin, hero], age:null}
		assertEquals(3, response.toJsonObject().asJsonArray("name").size());
	}

	public void testComposite() throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				WriteNode bleujin = wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20);
				bleujin.child("hero").property("name", "hero").property("age", 21).parent().child("jin").property("name", "jin");
				return null;
			}
		});

		ReadNode bleujin = session.pathBy("/bleujin");

		TalkResponse response = newBuilder.newInner().inlist("children", bleujin.children(), "name, age").build();
		assertEquals(2, response.toJsonObject().asJsonArray("children").size());

		JsonArray array = newBuilder.newInlist(bleujin.children(), "name, age").build().toJsonArray();
		assertEquals(2, array.size());

		array = newBuilder.newInlist(bleujin.refs("friends"), "name, age").build().toJsonArray();
		assertEquals(0, array.size());

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				WriteNode bleujin = wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20).refTos("friends", "/bleujin/hero", "/bleujin/jin");
				return null;
			}
		});
		array = newBuilder.newInlist(bleujin.refs("friends"), "name, age").build().toJsonArray();
		assertEquals(2, array.size());

		
		
	}

}
