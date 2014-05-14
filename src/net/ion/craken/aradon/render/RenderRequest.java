package net.ion.craken.aradon.render;

import java.util.List;

import org.apache.lucene.analysis.kr.utils.StringUtil;

import com.google.common.collect.Lists;

public class RenderRequest {

	private String nodePath;
	private boolean isChildrenRequest;
	private String templateFile;
	private List<String> propsToRender;
    private Renderer.RenderType type;

    public String getNodePath() {
		return nodePath;
	}

	public boolean isChildrenRequest() {
		return isChildrenRequest;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public List<String> getPropsToRender() {
		return propsToRender;
	}

	public static RenderRequest create(String uri, Renderer.RenderType type) {
		RenderRequest renderRequest = new RenderRequest();
        renderRequest.type = type;
//		renderRequest.nodePath = StringUtil.removeEnd(StringUtil.substringBeforeLast(uri, "."), "/");
		renderRequest.nodePath = StringUtil.removeEnd(uri, "/");
		renderRequest.isChildrenRequest = uri.endsWith("/");
		renderRequest.templateFile = renderRequest.isChildrenRequest ? "children.tpl" : "explorer.tpl";

//		String[] props = StringUtil.split(uri, ",");
//		renderRequest.propsToRender = Lists.newArrayList(props);
//
//		if (renderRequest.propsToRender.size() > 0) {
//			renderRequest.templateFile = "edit_value.tpl";
//		}
		
		return renderRequest;
	}

    public Renderer.RenderType getType() {
        return type;
    }

    public void setTemplate(String templateFile) {
        this.templateFile = templateFile;
    }
}
