package net.ion.talk.responsebuilder;

import net.ion.craken.node.ReadNode;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.util.ObjectUtil;
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

    public static TalkResponse makeResponse(String id, Object result) {
        return TalkResponseBuilder.create().newInner()
                .property("createAt", ToonServer.GMTTime())
                .property("status", "success")
                .property("result", result instanceof JsonElement ? result : ObjectUtil.toString(result, "undefined")).build();
    }
    
    public static BasicBuilder makeCommandBuilder(String script){
        return TalkResponseBuilder.create().newInner()
                .property("createAt", ToonServer.GMTTime())
                .property("status", "success")
                .property("script", script) ;
//                .property("result", result instanceof JsonElement ? result : ObjectUtil.toString(result, "undefined")).build();
    }


    public static TalkResponse failResponse(Exception e) {
        return TalkResponseBuilder.create().newInner()
                .property("status", "failure")
                .property("result", e.getMessage())
                .property("createdAt", ToonServer.GMTTime())
                .build();
    }


}
