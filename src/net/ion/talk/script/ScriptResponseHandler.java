package net.ion.talk.script;

import net.ion.talk.ParameterMap;


public interface ScriptResponseHandler<T> {
	public final static ScriptResponseHandler<Object> ReturnNative = new ScriptResponseHandler<Object>() {
		@Override
		public Object onSuccess(String fullName, ParameterMap pmap, Object result) {
			return result;
		}

		@Override
		public Object onThrow(String fullName, ParameterMap pmap, Exception ex) {
			ex.printStackTrace(); 
			return ex;
		}
	};

	public T onSuccess(String fullName, ParameterMap pmap, Object result) ;
	public T onThrow(String fullName, ParameterMap pmap, Exception ex) ;
}
