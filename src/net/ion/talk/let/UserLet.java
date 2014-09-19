package net.ion.talk.let;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.core.ContextParam;
import net.ion.talk.bean.Const;

import org.jboss.resteasy.spi.HttpRequest;

import com.google.common.base.Function;

public class UserLet {

	private ReadSession session;

	public UserLet(@ContextParam("repository") RepositoryEntry rentry) throws IOException {
		this.session = rentry.login();
	}

	@POST
	public String create(final @PathParam("email") String userId, @Context HttpRequest request) throws Exception {

		if (session.exists("/users/" + userId)) {
			throw new IllegalArgumentException("User already exists");
		}

		final MultivaluedMap<String, String> props = request.getFormParameters();

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				WriteNode node = wsession.pathBy("/users/" + userId);

				for (Entry<String, List<String>> entry : props.entrySet()) {
					String key = (String) entry.getKey();
					node.property(key, entry.getValue().get(0));
				}

				node.property(Const.User.UserId, userId);

				return null;
			}
		});

		return "success";
	}

	@GET
	public JsonObject retrieve(@PathParam("email") String userId) throws IOException {

		return session.ghostBy("/users/" + userId).transformer(new Function<ReadNode, JsonObject>() {
			@Override
			public JsonObject apply(ReadNode node) {
				JsonObject json = JsonObject.create();
				Set<PropertyId> propertyIDs = node.keys();

				for (PropertyId propertyId : propertyIDs) {
					String pid = propertyId.idString();
					if ("password".equals(pid))
						continue;

					json.put(pid, node.property(pid).asString());
				}

				return json;
			}
		});

	}

}