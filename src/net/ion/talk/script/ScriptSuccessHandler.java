package net.ion.talk.script;

import net.ion.talk.ParameterMap;

public interface ScriptSuccessHandler<T> {
	public final static ScriptSuccessHandler<Object> ReturnNative = new ScriptSuccessHandler<Object>() {
		@Override
		public Object success(Object result) {
			return result;
		}
	};

	public T success(Object result) ;
}
