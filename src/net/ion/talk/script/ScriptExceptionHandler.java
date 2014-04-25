package net.ion.talk.script;

import net.ion.talk.ParameterMap;

public interface ScriptExceptionHandler<T> {
	
	public final static ScriptExceptionHandler<Object> ReturnExceptionHandler = new ScriptExceptionHandler<Object>() {
		@Override
		public Exception ehandle(Exception ex, String fullFnName, ParameterMap params) {
			return ex;
		}
	};
	
	public final static ScriptExceptionHandler<Void> PrintExceptionHandler = new ScriptExceptionHandler<Void>() {
		@Override
		public Void ehandle(Exception ex, String fullFnName, ParameterMap params) {
			ex.printStackTrace();
			return null;
		}
	};
	
	public T ehandle(Exception ex, String fullFnName, ParameterMap params) ;
}
