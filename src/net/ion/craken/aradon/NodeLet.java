package net.ion.craken.aradon;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.render.Renderer;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.ContextParam;

@Path("/node/repository/{renderType}")
public class NodeLet {

	
	private ReadSession session;
	public NodeLet(@ContextParam("repository") RepositoryEntry repository) throws IOException{
		this.session = repository.login() ;
	}
	
	@GET
	@Path("/{remain : .*}")
	public Object pathBy(@PathParam("renderType") String renderType, @PathParam("remain") String remainPath) throws IOException {
		return Renderer.create(session).from("/" + remainPath, renderType).render();
	}

	@POST
	@Path("/{remain : .*}")
	public String upsert(@FormParam("body") String body, @PathParam("remain") String remainPath) throws Exception {
		upsertNode(session, remainPath, body);

		return "success";
	}

	@DELETE
	@Path("/{remain : .*}")
	public String delete(@PathParam("remain") String remainPath) throws Exception {

		final String nodePath = remainPath;
		deleteNode(session, nodePath);

		return "success";
	}

	void upsertNode(ReadSession session, final String nodePath, String body) throws Exception {
//		final String nodePath = getNodePathFrom(uri);
		final JsonObject json = JsonParser.fromString(body).getAsJsonObject();
		final String paramToUpdate = StringUtil.substringAfterLast(nodePath, ".");

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

	void deleteNode(ReadSession session, final String nodePath) throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy(nodePath).removeSelf();
				return null;
			}
		});
	}

//	private String getNodePathFrom(Request req) {
//		String requestURI = getRequestURIOnly(req);
//		return getNodePathFrom(requestURI);
//	}

	private String getNodePathFrom(String requestURI) {
		return StringUtil.substringBeforeLast(requestURI, ".");
	}

//	private String getRequestURIOnly(Request req) {
//		return StringUtils.substringBeforeLast(req.getResourceRef().getRemainingPart(), "?");
//	}
}
