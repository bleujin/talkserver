package net.ion.talk.handler;

import java.util.Set;

import net.ion.framework.util.SetUtil;
import net.ion.talk.TalkEngine;

public class TalkHandlerGroup {

	private Set<TalkHandler> handlers = SetUtil.newOrdereddSet() ;
	public static TalkHandlerGroup create() {
		return new TalkHandlerGroup();
	}

	public TalkHandlerGroup addHandler(TalkHandler thandler) {
		handlers.add(thandler) ;
        return this;
	}

	public void set(TalkEngine engine) {
		for (TalkHandler handler : handlers) {
			engine.registerHandler(handler) ;
		}
	}

}
