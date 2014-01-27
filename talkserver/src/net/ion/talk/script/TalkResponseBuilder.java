package net.ion.talk.script;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.framework.parse.gson.JsonObject;

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

}
