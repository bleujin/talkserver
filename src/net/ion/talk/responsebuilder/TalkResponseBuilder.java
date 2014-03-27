package net.ion.talk.responsebuilder;

import net.ion.craken.node.ReadNode;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.talk.ToonServer;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class TalkResponseBuilder {

    private TalkResponseBuilder() {
    }

    public static TalkResponseBuilder create(){
        return new TalkResponseBuilder();
    }

    public BasicBuilder newInner() {
        return new BasicBuilder(null);
    }

    public ListBuilder newInlist(){
        return new ListBuilder(null).next();
    }

	public AbstractBuilder newInlist(Iterable<ReadNode> nodes, String values) {
		ListBuilder created = new ListBuilder(null);
		for (ReadNode node : nodes) {
			created.next().property(node, values) ;
		}
		return created;
	}

    public static String makeResponse(String id, Object result) {

        result = result instanceof JsonElement ? result : result.toString();

        BasicBuilder response = TalkResponseBuilder.create().newInner()
                .property("createAt", ToonServer.GMTTime())
//                .property("id", id)
                .property("result", result);

        if (result instanceof Throwable)
            response.property("status", "failure");
        else
            response.property("status", "success");

        return response.build().toString();
    }

    public static String makeResponse(Exception e) {
        return TalkResponseBuilder.create().newInner()
                .property("status", "failure")
                .property("result", e.toString())
                .property("createdAt", ToonServer.GMTTime())
                .build().toString();
    }


}
