package net.ion.craken.aradon;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import net.ion.craken.aradon.render.Renderer;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.Repository;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;

import org.apache.commons.lang.StringUtils;
import org.restlet.Request;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class NodeLet implements IServiceLet {

	@Get
	public Representation pathBy(@ContextParam("repository") Repository repository, @PathParam("workspace") String workspace, @PathParam("renderType") String renderType, @AnRequest Request req) throws IOException {
		ReadSession session = repository.login(workspace);
		String uri = getRequestURIOnly(req);
		
		return Renderer.create(session).from(uri, renderType).render();
	}

	@Post
	public String upsert(@ContextParam("repository") Repository repository, @PathParam("workspace") String workspace, @FormParam("body") String body, @AnRequest Request req) throws Exception {
		String uri = getRequestURIOnly(req);

		upsertNode(repository, workspace, uri, body);

		return "success";
	}

	@Delete
	public String delete(@ContextParam("repository") Repository repository, @PathParam("workspace") String workspace, @AnRequest Request req) throws Exception {

		final String nodePath = getNodePathFrom(req);

		deleteNode(repository, workspace, nodePath);

		return "success";
	}

	void upsertNode(Repository repository, String workspace, String uri, String body) throws Exception {
		final String nodePath = getNodePathFrom(uri);
		final JsonObject json = JsonParser.fromString(body).getAsJsonObject();
		final String paramToUpdate = StringUtil.substringAfterLast(uri, ".");

		ReadSession session = repository.login(workspace);

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				WriteNode wnode = wsession.pathBy(nodePath);

				if(StringUtil.isNotBlank(paramToUpdate)) {
                    JsonElement element = json.get(paramToUpdate);
                    updateOnly(wnode, paramToUpdate, element);
				} else {
					updateAll(wnode, json);
				}

				return null;
			}

			private void updateOnly(WriteNode wnode, String propertyId, JsonElement element) {
                setValue(wnode, propertyId, element);
			}

			private void updateAll(WriteNode wnode, JsonObject json) {
				unsetRemoved(json, wnode);
				setProperties(json, wnode);
			}

			private void setProperties(JsonObject json, WriteNode wnode) {
				for (Entry<String, JsonElement> entry : json.entrySet()) {
					JsonElement element = entry.getValue();

					if ("blob".equals(entry.getKey())) {
						continue;
					}

                    setValue(wnode, entry.getKey(), element);
                }
			}

            private void setValue(WriteNode wnode, String propertyId, JsonElement element) {
                if (element.isJsonPrimitive()) {
                    wnode.property(propertyId, element.getAsJsonPrimitive().getValue());
                } else if (element.isJsonArray()) {
                    wnode.unset(propertyId);
                    wnode.property(propertyId, element.getAsJsonArray().toObjectArray());
                } else {
                    throw new IllegalArgumentException("Invalid Property type found : " + propertyId);
                }
            }

            private void unsetRemoved(JsonObject json, WriteNode wnode) {
				Set<PropertyId> keys = wnode.keys();
				for(PropertyId key : keys) {
					String pId = key.idString();
					if(json.get(pId) == null) {
						wnode.unset(pId);
					}
				}
			}
		}, InternalServerErrorHandler.DEFAULT);
	}

	void deleteNode(Repository repository, String workspace, final String nodePath) throws Exception {
		ReadSession session = repository.login(workspace);
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy(nodePath).removeSelf();
				return null;
			}
		});
	}

	private String getNodePathFrom(Request req) {
		String requestURI = getRequestURIOnly(req);
		return getNodePathFrom(requestURI);
	}

	private String getNodePathFrom(String requestURI) {
		return StringUtil.substringBeforeLast(requestURI, ".");
	}

	private String getRequestURIOnly(Request req) {
		return StringUtils.removeEnd(req.getResourceRef().getRemainingPart(), "?");
	}
}
