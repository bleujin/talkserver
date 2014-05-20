package net.ion.talk.let;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.MultiValueMap;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.talk.bean.Const;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.google.common.base.Function;

public class UserLet implements IServiceLet {

	@Post
	public String create(@ContextParam("repository") RepositoryEntry rentry, final @PathParam("email") String userId, @AnRequest InnerRequest request) throws Exception {
		ReadSession session = rentry.login();

		if (session.exists("/users/" + userId)) {
			throw new IllegalArgumentException("User already exists");
		}

		final MultiValueMap props = request.getFormParameter();

		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				WriteNode node = wsession.pathBy("/users/" + userId);
				
				for (Entry entry : props.entrySet()) {
					String key = (String) entry.getKey();
					node.property(key, entry.getValue());
				}
				
				node.property(Const.User.UserId, userId);
				
				return null;
			}
		});

		return "success";
	}

	@Get
	public JsonObjectRepresentation retrieve(@ContextParam("repository") RepositoryEntry rentry, @PathParam("email") String userId) throws IOException {
		ReadSession session = rentry.login();
		
		return session.ghostBy("/users/" + userId).transformer(new Function<ReadNode, JsonObjectRepresentation>() {
			@Override
			public JsonObjectRepresentation apply(ReadNode node) {
				JsonObject json = JsonObject.create();
				Set<PropertyId> propertyIDs = node.keys();
				
				for (PropertyId propertyId : propertyIDs) {
					String pid = propertyId.idString();
					if("password".equals(pid)) continue;
					
					json.put(pid, node.property(pid).asString());
				}

				return new JsonObjectRepresentation(json);
			}
		});

	}

}