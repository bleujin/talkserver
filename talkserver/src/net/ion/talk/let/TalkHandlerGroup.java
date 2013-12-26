package net.ion.talk.let;

import java.util.Set;

import net.ion.framework.util.SetUtil;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkHandler;

public class TalkHandlerGroup {

	private Set<TalkHandler> handlers = SetUtil.newOrdereddSet() ;
	public static TalkHandlerGroup create() {
		return new TalkHandlerGroup();
	}

	public void addHandler(TalkHandler thandler) {
		handlers.add(thandler) ;
	}

	public void set(TalkEngine engine) {
		for (TalkHandler handler : handlers) {
			engine.registerHandler(handler) ;
		}
	}

}
