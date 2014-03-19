package net.ion.craken.aradon.render;

import junit.framework.TestCase;
import net.ion.craken.aradon.render.RenderRequest;
import net.ion.craken.aradon.render.Renderer;

public class TestRenderRequest extends TestCase {

	public void testParseURI_json() {
		RenderRequest request = RenderRequest.create("/airkjh", Renderer.RenderType.json);

		assertEquals("/airkjh", request.getNodePath());
		assertEquals(false, request.isChildrenRequest());
		assertEquals(0, request.getPropsToRender().size());
		assertEquals("json", request.getType().name());
	}

	public void testParseURI_html() {
		RenderRequest request = RenderRequest.create("/airkjh", Renderer.RenderType.html);

		assertEquals("/airkjh", request.getNodePath());
		assertEquals(false, request.isChildrenRequest());
		assertEquals("explorer.tpl", request.getTemplateFile());
		assertEquals("html", request.getType().name());
	}

	public void testParseURI_children_json() {
		RenderRequest request = RenderRequest.create("/airkjh/", Renderer.RenderType.json);

		assertEquals("/airkjh", request.getNodePath());
		assertEquals(true, request.isChildrenRequest());
	}

	public void testParseURI_children_html() {
		RenderRequest request = RenderRequest.create("/airkjh/", Renderer.RenderType.html);

		assertEquals("/airkjh", request.getNodePath());
		assertEquals(true, request.isChildrenRequest());
		assertEquals("children.tpl", request.getTemplateFile());
		assertEquals("html", request.getType().name());
	}

	public void testParseURI_props() {
		RenderRequest request = RenderRequest.create("/airkjh.name", Renderer.RenderType.html);

		assertEquals("/airkjh", request.getNodePath());
		assertEquals(false, request.isChildrenRequest());
		assertEquals("edit_value.tpl", request.getTemplateFile());
		assertEquals(1, request.getPropsToRender().size());
		assertEquals("name", request.getPropsToRender().get(0));
		assertEquals("html", request.getType().name());	}

	public void testParseURI_multiProps() {
		RenderRequest request = RenderRequest.create("/airkjh.name,prop1,prop2", Renderer.RenderType.html);

		assertEquals("/airkjh", request.getNodePath());
		assertEquals(false, request.isChildrenRequest());
		assertEquals("edit_value.tpl", request.getTemplateFile());
		assertEquals(3, request.getPropsToRender().size());
		assertEquals("name", request.getPropsToRender().get(0));
		assertEquals("prop1", request.getPropsToRender().get(1));
		assertEquals("prop2", request.getPropsToRender().get(2));
		assertEquals("html", request.getType().name());
	}	
	
}
