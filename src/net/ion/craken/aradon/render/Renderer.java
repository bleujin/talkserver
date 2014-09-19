package net.ion.craken.aradon.render;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.Workspace;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.mte.Engine;
import net.ion.framework.parse.gson.GsonBuilder;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;

import com.google.common.base.Function;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Renderer {

    // TODO - RenderType + isChildrenRequest를 묶어서 하나의 Type으로
	// TODO - Type을 만들고 equals를 override해서 Table 대신 일반적인 Map을 사용하도록
	static Table<RenderType, Boolean, RenderFunction> renderers = HashBasedTable.create();
    static {
        renderers.put(RenderType.json, false, new NodeJsonRenderFunction());
        renderers.put(RenderType.json, true, new ChildrenJson());
        renderers.put(RenderType.html, false, new NodeHtmlFunction());
        renderers.put(RenderType.html, true, new ChildrenHtmlFunction());
    }

	private ReadSession session;
    private RenderRequest renderRequest;

    public static enum RenderType {
		json, html
	}

	private Renderer(ReadSession session) {
		this.session = session;
	}

	public static Renderer create(ReadSession session) {
		return new Renderer(session);
	}

	public Renderer from(String uri, String type) {
		return from(uri, RenderType.valueOf(type));
	}

	public Renderer from(String uri, RenderType type) {
        this.renderRequest = RenderRequest.create(uri, type);
		return this;
	}

	public Renderer template(String fileName) {
        this.renderRequest.setTemplate(fileName);
		return this;
	}

	public Object render() {
		ReadNode node = session.ghostBy(renderRequest.getNodePath());

		Function<ReadNode, ? extends Object> transformer = findSuitableTransformer();

		return node.transformer(transformer);
	}

    Function<ReadNode, ? extends Object> findSuitableTransformer() {
        RenderType renderType = renderRequest.getType();
		boolean isChildrenRequest = renderRequest.isChildrenRequest();
		
		return renderers.get(renderType, isChildrenRequest).getTransformer(renderRequest);
    }


	static Function<ReadNode, JsonObject> transformToJson(final List<String> props) {
		return new Function<ReadNode, JsonObject>() {

			@Override
			public JsonObject apply(ReadNode node) {
				JsonObject json = new JsonObject();
				if (isPropsSpecified(props)) {
					renderSpecifiedOnly(node, json, props);
				} else {
					renderAllProps(node, json);
				}

				return json;
			}

			private void renderAllProps(ReadNode node, JsonObject json) {
				Iterator<PropertyId> iterator = node.keys().iterator();
				while (iterator.hasNext()) {
					PropertyId propertyId = iterator.next();
					PropertyValue pValue = node.propertyId(propertyId);

					put(node, json, propertyId.idString(), pValue);
				}
			}

			private void renderSpecifiedOnly(ReadNode node, JsonObject json, final List<String> props) {
				for (String propertyId : props) {
					PropertyValue propertyValue = node.property(propertyId);
					put(node, json, propertyId, propertyValue);
				}
			}

			private boolean isPropsSpecified(List<String> props) {
				return props.size() > 0;
			}

			private void put(ReadNode node, JsonObject json, String propertyId, PropertyValue propertyValue) {
				if ("blob".equals(propertyId)) {
					json.put("blob", JsonObject.fromString(node.property("blob").stringValue()));
				} else if (propertyValue.asSet().size() > 1) {
					json.put(propertyId, propertyValue.asJsonArray());
				} else {
					json.put(propertyId, propertyValue.value());
				}
			}

		};
	}
}


class NodeJsonRenderFunction implements RenderFunction {

	@Override
	public Function<ReadNode, ? extends Object> getTransformer(final RenderRequest renderRequest) {
		return new Function<ReadNode, JsonObject>() {
			@Override
			public JsonObject apply(ReadNode node) {
				return Renderer.transformToJson(renderRequest.getPropsToRender()).apply(node);
			}
		};
	}
}

class NodeHtmlFunction implements RenderFunction {

    @Override
    public Function<ReadNode, Response> getTransformer(final RenderRequest request) {
    	

        return new Function<ReadNode, Response>() {
            @Override
            public Response apply(ReadNode node) {
                Workspace workspace = ((ReadSession) node.session()).workspace();
                Engine parseEngine = workspace.parseEngine();
                String wsName = workspace.wsName();

                try {
                    String template = IOUtil.toStringWithClose(Renderer.class.getResourceAsStream(request.getTemplateFile()));
                    JsonObject json = Renderer.transformToJson(request.getPropsToRender()).apply(node);

                    Map<String, Object> propertyMap = MapUtil.chainKeyMap().put("self", node).put("transformed", json.toString()).put("workspace", wsName).toMap();

                    for (String propertyId : request.getPropsToRender()) {
                        if (!"blob".equalsIgnoreCase(propertyId)) {
                            String value;

                            if (json.get(propertyId).isJsonNull()) {
                                value = "";
                            } else {
                                value = json.get(propertyId).getAsString();
                            }

                            propertyMap.put(propertyId, value);
                        }
                    }

                    String transformed = parseEngine.transform(template, propertyMap);

                    return Response.ok().entity(transformed).type(MediaType.TEXT_HTML).build() ;

                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}

class ChildrenJson implements RenderFunction {

	@Override
	public Function<ReadNode, JsonArray> getTransformer(final RenderRequest renderRequest) {
		return new Function<ReadNode, JsonArray>() {
			@Override
			public JsonArray apply(ReadNode node) {
				JsonArray children = new JsonArray();
				ReadChildren readChildren = node.children();

				for (ReadNode rnode : readChildren) {
					children.add(rnode.fqn().toJson());
				}

				return children;
			}
		};
	}
}

class ChildrenHtmlFunction implements RenderFunction {

    @Override
    public Function<ReadNode, Response> getTransformer(final RenderRequest request) {
        return new Function<ReadNode, Response>() {
            @Override
            public Response apply(ReadNode node) {
                Workspace workspace = ((ReadSession) node.session()).workspace();
                Engine parseEngine = workspace.parseEngine();
                String wsName = workspace.wsName();

                try {
                    String template = IOUtil.toStringWithClose(Renderer.class.getResourceAsStream(request.getTemplateFile()));

                    String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(node.toValueJson()) ;
                    
                    
                    Map<String, Object> propertyMap = MapUtil.chainKeyMap()
                    			.put("parent", node)
                    			.put("selfjson", jsonString)
                    			.put("children", node.children()).put("workspace", wsName).toMap();
                    String transformed = parseEngine.transform(template, propertyMap);

                    return Response.ok().entity(transformed).type(MediaType.TEXT_HTML).build();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }


            }
        };
    }
}