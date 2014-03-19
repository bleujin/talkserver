package net.ion.craken.aradon.render;

import net.ion.craken.aradon.TestCrakenBase;
import net.ion.craken.aradon.render.Renderer;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.radon.core.representation.JsonObjectRepresentation;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

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
		JsonObjectRepresentation rep = (JsonObjectRepresentation) renderer.from("/airkjh", "json").render();
		JsonObject json = rep.getJsonObject();

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
		JsonObjectRepresentation rep = (JsonObjectRepresentation) renderer.from("/airkjh", "json").render();
		JsonObject json = rep.getJsonObject();

		assertEquals("airkjh", json.get("name").getAsString());
		assertEquals(100, json.get("int").getAsInt());
		assertEquals(false, json.get("boolean").getAsBoolean());
	}

	public void testSingleProp() {
		JsonObjectRepresentation rep = (JsonObjectRepresentation) renderer.from("/airkjh.name", "json").render();
		JsonObject json = rep.getJsonObject();

		assertEquals("airkjh", json.get("name").getAsString());
		assertEquals(null, json.get("int"));
		assertEquals(null, json.get("boolean"));
	}

	public void testMultiProp() {
		JsonObjectRepresentation rep = (JsonObjectRepresentation) renderer.from("/airkjh.name,boolean", "json").render();
		JsonObject json = rep.getJsonObject();

		assertEquals("airkjh", json.get("name").getAsString());
		assertEquals(null, json.get("int"));
		assertEquals(false, json.get("boolean").getAsBoolean());
	}

	public void testAllPropsToHtml() {
		StringRepresentation rep = (StringRepresentation) renderer.from("/airkjh", "html").template("test-all-prop.tpl").render();

		assertEquals(MediaType.TEXT_HTML, rep.getMediaType());
		Debug.line(rep.getText());
	}

	public void testOnePropToHtml() {
		StringRepresentation rep = (StringRepresentation) renderer.from("/airkjh.name", "html").template("test-single-prop.tpl").render();

		assertEquals(MediaType.TEXT_HTML, rep.getMediaType());
		assertEquals("<body>name:airkjh</body>", rep.getText());
	}

	public void testNoPropsNoTemplateSpecified() {
		StringRepresentation rep = (StringRepresentation) renderer.from("/airkjh", "html").render();

		assertTrue(rep.getText().startsWith("<!--explorer.tpl-->"));
	}

	public void testPropsNoTemplate() {
		StringRepresentation rep = (StringRepresentation) renderer.from("/airkjh.name", "html").render();

		assertTrue(rep.getText().startsWith("<!--edit_value.tpl-->"));
	}

	public void testChildren() throws Exception {
		addNode("/airkjh/child1", "/airkjh/child2");

		JsonObjectRepresentation representation = (JsonObjectRepresentation) renderer.from("/airkjh/", "json").render();
		JsonArray array = representation.getJsonArray();

		assertEquals(2, array.size());
	}

	public void testChildrenHtml() throws Exception {
		addNode("/airkjh/child1", "/airkjh/child2");

		StringRepresentation rep = (StringRepresentation) renderer.from("/airkjh/", "html").render();

		assertTrue(rep.getText().startsWith("<!--children.tpl-->"));
	}

	public void testNotExistNode() {
		JsonObjectRepresentation rep = (JsonObjectRepresentation) renderer.from("airkjh2", "json").render();
		JsonObject json = rep.getJsonObject();

		assertEquals("{}", json.toString());
	}

	public void testNotExistProperty_json() throws Exception {
		// when property doesn't exist is request in html, the property is
		// rendered to null
		JsonObjectRepresentation rep = (JsonObjectRepresentation) renderer.from("/airkjh.age", "json").render();
		JsonObject actual = rep.getJsonObject();

		JsonObject expected = new JsonObject();
		expected.put("age", null);

		assertEquals(expected.toString(), actual.toString());
	}

	public void testNotExistProperty_html() {
		// when property doesn't exist is request in html, the property is
		// rendered to blank
		StringRepresentation rep = (StringRepresentation) renderer.from("/airkjh.age", "html").template("test-single-prop.tpl").render();

		assertEquals("<body>name:</body>", rep.getText());
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
