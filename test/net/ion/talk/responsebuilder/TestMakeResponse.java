package net.ion.talk.responsebuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.Debug;
import net.ion.talk.responsebuilder.AbstractBuilder;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

/**
 * Author: Ryunhee Han Date: 2014. 1. 14.
 */
public class TestMakeResponse extends TestCase {

	private TalkResponseBuilder newBuilder;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.newBuilder = TalkResponseBuilder.create();
	}

	public void testRoot() throws ExecutionException {
		AbstractBuilder child = newBuilder.newInner().inner("child");

		assertEquals(true, child.parent().isRoot());
	}

	public void testMakeFlat() throws Exception {
		// {"age":20,"name":"bleujin"}
		TalkResponse response = newBuilder.newInner().property("name", "bleujin").property("age", 20).build();
		JsonObject json = response.toJsonObject();

		assertEquals("bleujin", json.asString("name"));
		assertEquals(20, json.asInt("age"));
	}

	public void testArray() throws Exception {
		// [{"age":20,"name":"bleujin"},{"name":"hero"}]
		TalkResponse response = newBuilder.newInlist().property("name", "bleujin").property("age", 20).next().property("name", "hero").build();
		JsonArray json = response.toJsonArray();

		assertEquals("bleujin", json.toArray()[0].getAsJsonObject().asString("name"));
		assertEquals("hero", json.toArray()[1].getAsJsonObject().asString("name"));
	}

	public void testComposite() throws Exception {
		// {"age":20,"name":"bleujin", "children":[{"name":"jin", "age":15}, {"name":"hero"}]}

		TalkResponse response = newBuilder.newInner().property("name", "bleujin").property("age", 20).inlist("children").property("name", "jin").property("age", 15).next().property("name", "hero").build();

		String exp1 = response.toJsonElement().toString();

		response = newBuilder.newInner().property("name", "bleujin").property("age", 20).inlist("children").property("name", "jin").property("age", 15).parent().inlist("children").property("name", "hero").build();

		String exp2 = response.toJsonElement().toString();
		assertEquals(exp1, exp2);
	}

	public void testInInner() throws Exception {
		// {name:bleujin, age:21, bf:{name:jin, age:20}}
		TalkResponse response = newBuilder.newInner().property("name", "bleujin").property("age", "21").inner("bf").property("name", "jin").property("age", 20).build();

		assertEquals(20, JsonUtil.findSimpleObject(response.toJsonObject(), "bf.age"));
	}

}
