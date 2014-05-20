package net.ion.talk.script;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.script.FileAlterationMonitor;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.UserConnection;
import net.ion.talk.handler.engine.CommandParam;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import net.ion.talk.toonweb.ChatServer;
import net.ion.talk.toonweb.ChatClient;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

import sun.org.mozilla.javascript.internal.NativeObject;

public class CommandScript {

	private ScriptEngine sengine;
	private Map<String, Object> packages = MapUtil.newCaseInsensitiveMap();
	private FileAlterationMonitor monitor;
	private ScheduledExecutorService ses;
	private ReadSession rsession;

	private CommandScript(ReadSession rsession, ScheduledExecutorService ses) {
		ScriptEngineManager manager = new ScriptEngineManager();
		this.ses = ses ;
		this.sengine = manager.getEngineByName("JavaScript");
		sengine.put("session", rsession);
		sengine.put("rb", TalkResponseBuilder.create()) ;
		
		this.rsession = rsession ;
	}

	public static CommandScript create(ReadSession rsession, ScheduledExecutorService ses) {
		return new CommandScript(rsession, ses);
	}

	public CommandScript readDir(final File scriptDir) throws IOException {
		return readDir(scriptDir, false) ;
	}
	
	public ReadSession session(){
		return rsession ;
	}
	
	public CommandScript readDir(final File scriptDir, boolean reloadWhenDetected) throws IOException {
		if (!scriptDir.exists() || !scriptDir.isDirectory())
			throw new IllegalArgumentException(scriptDir + " is not directory");

		try {
			if (this.monitor != null)
			this.monitor.stop();
		} catch (Exception e) {
			throw new IOException(e) ;
		} 
			
		String scriptExtension = ".script";
		
		new DirectoryWalker<String>(FileFilterUtils.suffixFileFilter(scriptExtension), 2) {
			protected void handleFile(File file, int dept, Collection<String> results) throws IOException {
				String packName = loadPackageScript(file);
				results.add(packName);
			}

			protected boolean handleDirectory(File dir, int depth, Collection results) {
				return true;
			}

			public List<String> loadScript(File scriptDir) throws IOException {
				List<String> result = ListUtil.newList();
				super.walk(scriptDir, result);
				return result;
			}

		}.loadScript(scriptDir);
		
		if (! reloadWhenDetected) return this ;

		
		FileAlterationObserver observer = new FileAlterationObserver(scriptDir, FileFilterUtils.suffixFileFilter(scriptExtension)) ;
		observer.addListener(new FileAlterationListenerAdaptor() {
			@Override
			public void onFileDelete(File file) {
				Debug.line("Bot Deleted", file);
				packages.remove(FilenameUtils.getBaseName(file.getName())) ;
			}
			
			@Override
			public void onFileCreate(File file) {
				Debug.line("Bot Created", file);
				loadPackageScript(file) ;
			}
			
			@Override
			public void onFileChange(File file) {
				Debug.line("Bot Changed", file);
				loadPackageScript(file) ;
			}
		});
		
		try {
			observer.initialize();

			this.monitor = new FileAlterationMonitor(1000, this.ses, observer) ;
			monitor.start(); 
		} catch (Exception e) {
			throw new IOException(e) ;
		} 

		return this;
	}


	private String loadPackageScript(File file)  {
		String packName = FilenameUtils.getBaseName(file.getName());
		try {
			String script = FileUtil.readFileToString(file);
			packages.put(packName, sengine.eval(script));
		} catch (IOException e) {
			e.printStackTrace(); 
		} catch (ScriptException e) {
			e.printStackTrace(); 
		}
		return packName;
	}

	public CommandScript addScript(String packName, String script) throws ScriptException{
		packages.put(packName, sengine.eval(script)) ;
		return this ;
	}
	
	
	public Map<String, Object> packages() {
		return Collections.unmodifiableMap(packages);
	}

	public List<String> fullFnNames() {
		List<String> result = ListUtil.newList() ;
		for(String pkgName : packages.keySet()){
			String[] fnNames = fnNames(pkgName) ;
			for (String fnName : fnNames) {
				result.add(pkgName + "." + fnName) ;
			}
		}
		return result;
	}

	
	public String[] fnNames(String packName){
		if (! packages.containsKey(packName)) return new String[0] ;
		
		NativeObject no = (NativeObject) packages.get(packName) ;

		Object[] fns = no.getAllIds();
		return StringUtil.split(StringUtil.join(fns, ','), ',') ;
	}

	
	private <T> T callFn(String fullFnName, UserConnection source, CommandParam cparam, CommandResponseHandler<T> returnnative) {
		try {
			
			String[] names = StringUtil.split(fullFnName, '.') ;
			
			String packName = names[0];
			String fnName = names[1];

			Object pack = packages.get(packName);
			if (pack == null) return returnnative.onThrow(fnName, cparam, new IOException("not found package : " + fnName)) ;

			Object result = ((Invocable) sengine).invokeMethod(pack, fnName, source, cparam);
			if(result instanceof NativeJavaObject) result = ((NativeJavaObject)result).unwrap() ;  
			result = ObjectUtil.coalesce(result, "undefined") ;
			  
			return returnnative.onSuccess(fullFnName, cparam, result);
		} catch (ScriptException ex) {
			return returnnative.onThrow(fullFnName, cparam, ex) ;
		} catch (IndexOutOfBoundsException ex) {
			return returnnative.onThrow(fullFnName, cparam, ex) ;
		} catch (NoSuchMethodException ex) {
			return returnnative.onThrow(fullFnName, cparam, ex) ;
		}
	}

	public Object outroomFn(CommandParam cparam, UserConnection source) {
		return callFn("outroom." + cparam.fnName(), source, cparam, CommandResponseHandler.ReturnNative) ;
	}

}


interface CommandResponseHandler<T> {
	public final static CommandResponseHandler<Object> ReturnNative = new CommandResponseHandler<Object>() {
		@Override
		public Object onSuccess(String fullName, CommandParam cparam, Object result) {
			return result;
		}

		@Override
		public Object onThrow(String fullName, CommandParam cparam, Exception ex) {
			ex.printStackTrace(); 
			return ex;
		}
	};

	public T onSuccess(String fullName, CommandParam cparam, Object result) ;
	public T onThrow(String fullName, CommandParam cparam, Exception ex) ;
}

