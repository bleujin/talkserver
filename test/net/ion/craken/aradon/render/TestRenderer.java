package net.ion.craken.aradon.render;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ion.craken.aradon.TestCrakenBase;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;

public class TestRenderer extends TestCrakenBase {

	private Renderer renderer;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/airkjh").property("name", "airkjh").property("int", 100).property("boolean", false);
				return null;
			}
		});

		renderer = Renderer.create(session);
	}

	public void testFirst() {
		JsonObject json = (JsonObject) renderer.from("/airkjh", "json").render();

		assertEquals("airkjh", json.get("name").getAsString());
	}
	
	public void testInvalidRenderType() {
		try {
			renderer.from("", "xml");
			fail();
		} catch(IllegalArgumentException expect) {
			
		}
	}

	public void testAllProps() {
		JsonObject json = (JsonObject) renderer.from("/airkjh", "json").render();

		assertEquals("airkjh", json.get("name").getAsString());
		assertEquals(100, json.get("int").getAsInt());
		assertEquals(false, json.get("boolean").getAsBoolean());
	}

	public void testSingleProp() {
		JsonObject json = (JsonObject) renderer.from("/airkjh.name", "json").render();

		assertEquals("airkjh", json.get("name").getAsString());
		assertEquals(null, json.get("int"));
		assertEquals(null, json.get("boolean"));
	}

	public void testMultiProp() {
		JsonObject json = (JsonObject) renderer.from("/airkjh.name,boolean", "json").render();

		assertEquals("airkjh", json.get("name").getAsString());
		assertEquals(null, json.get("int"));
		assertEquals(false, json.get("boolean").getAsBoolean());
	}

	public void testNotExistNode() {
		JsonObject json = (JsonObject) renderer.from("airkjh2", "json").render();
		
		assertEquals("{}", json.toString());
	}

	public void testNotExistProperty_json() throws Exception {
		// when property doesn't exist is request in html, the property is
		// rendered to null
		JsonObject actual = (JsonObject) renderer.from("/airkjh.age", "json").render();

		JsonObject expected = new JsonObject();
		expected.put("age", null);

		assertEquals(expected.toString(), actual.toString());
	}

	public void testAllPropsToHtml() {
		Response rep = (Response) renderer.from("/airkjh", "html").template("test-all-prop.tpl").render();
		Debug.line(rep.getEntity());
	}

	public void testOnePropToHtml() {
		Response rep = (Response) renderer.from("/airkjh.name", "html").template("test-single-prop.tpl").render();
		assertEquals("<body>name:airkjh</body>", rep.getEntity());
	}

	public void testNoPropsNoTemplateSpecified() {
		Response rep = (Response) renderer.from("/airkjh", "html").render();

		assertTrue(rep.getEntity().toString().startsWith("<!--explorer.tpl-->"));
	}

	public void testPropsNoTemplate() {
		Response rep = (Response) renderer.from("/airkjh.name", "html").render();

		assertTrue(rep.getEntity().toString().startsWith("<!--edit_value.tpl-->"));
	}

	public void testChildren() throws Exception {
		addNode("/airkjh/child1", "/airkjh/child2");

		JsonArray array = (JsonArray) renderer.from("/airkjh/", "json").render();
		assertEquals(2, array.size());
	}

	public void testChildrenHtml() throws Exception {
		addNode("/airkjh/child1", "/airkjh/child2");

		Response rep = (Response) renderer.from("/airkjh/", "html").render();

		assertTrue(rep.getEntity().toString().startsWith("<!--children.tpl-->"));
	}

	public void testNotExistProperty_html() {
		// when property doesn't exist is request in html, the property is
		// rendered to blank
		Response rep = (Response) renderer.from("/airkjh.age", "html").template("test-single-prop.tpl").render();

		assertEquals("<body>name:</body>", rep.getEntity());
	}

	private void addNode(final String... paths) throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				for (String path : paths) {
					wsession.pathBy(path);
				}
				return null;
			}
		});
	}

}
