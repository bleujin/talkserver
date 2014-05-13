package net.ion.talk.script;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.script.FileAlterationMonitor;
import net.ion.framework.db.Rows;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.ParameterMap;
import net.ion.talk.bot.connect.BotClient;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

import sun.org.mozilla.javascript.internal.NativeObject;

public class BotScript {

	public static final BotScript DUMMY = null;
	
	private ScriptEngine sengine;
	private Map<String, Object> packages = MapUtil.newCaseInsensitiveMap();
	private FileAlterationMonitor monitor;
	private ScheduledExecutorService ses;
	private ReadSession rsession;

	private BotScript(ReadSession rsession, ScheduledExecutorService ses, NewClient nc) {
		ScriptEngineManager manager = new ScriptEngineManager();
		this.ses = ses ;
		this.sengine = manager.getEngineByName("JavaScript");
		sengine.put("session", rsession);
		sengine.put("rb", TalkResponseBuilder.create()) ;
		sengine.put("nc", nc) ;
		sengine.put("bc", BotClient.create(nc));
		
		this.rsession = rsession ;
	}

	public static BotScript create(ReadSession rsession, ScheduledExecutorService ses, NewClient nc) {
		return new BotScript(rsession, ses, nc);
	}

	public BotScript readDir(final File scriptDir) throws IOException {
		return readDir(scriptDir, false) ;
	}
	
	public ReadSession session(){
		return rsession ;
	}
	
	public BotScript readDir(final File scriptDir, boolean reloadWhenDetected) throws IOException {
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
		
		rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				for (String packName : packages.keySet()) {
					wsession.pathBy("/bots/" + packName).refTo("user", "/users/" + packName) ;
					wsession.pathBy("/users/" + packName) ;
				}
				return null;
			}
		}) ;
		
		for (String packName : packages.keySet()) {
			try {
				Object pack = packages.get(packName);
				((Invocable) sengine).invokeMethod(pack, "onLoad");
			} catch (ScriptException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		
		
		
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

	public BotScript addScript(String packName, String script) throws ScriptException{
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

	
	public <T> T callFn(String fullFnName, BotMessage bm, BotResponseHandler<T> returnnative) {
		try {
			
			String[] names = StringUtil.split(fullFnName, '.') ;
			
			String packName = names[0];
			String fnName = names[1];

			Object pack = packages.get(packName);
			if (pack == null) return returnnative.onThrow(fullFnName, bm, new IOException("not found package : " + fullFnName)) ;

			Object result = ((Invocable) sengine).invokeMethod(pack, fnName, bm);
			if(result instanceof NativeJavaObject) result = ((NativeJavaObject)result).unwrap() ;  
			result = ObjectUtil.coalesce(result, "undefined") ;
			  
			return returnnative.onSuccess(fullFnName, bm, result);
		} catch (ScriptException ex) {
			return returnnative.onThrow(fullFnName, bm, ex) ;
		} catch (IndexOutOfBoundsException ex) {
			return returnnative.onThrow(fullFnName, bm, ex) ;
		} catch (NoSuchMethodException ex) {
			return returnnative.onThrow(fullFnName, bm, ex) ;
		}
		
	}

	public Object callFn(String fullFnName,  BotMessage bm) {
		return callFn(fullFnName, bm,  BotResponseHandler.ReturnNative) ;
	}

	public Rows viewRows(ReadSession session, String script) throws ScriptException {
		ScriptContext bindings = new SimpleScriptContext();
		bindings.setAttribute("session", session, ScriptContext.ENGINE_SCOPE);
		Object result = sengine.eval(script, bindings) ;
		if (result instanceof Rows) return Rows.class.cast(result) ;
		throw new ScriptException("return type must be rows.class") ;
	}
}


interface BotResponseHandler<T> {
	public final static BotResponseHandler<Object> ReturnNative = new BotResponseHandler<Object>() {
		@Override
		public Object onSuccess(String fullName, BotMessage pmap, Object result) {
			return result;
		}

		@Override
		public Object onThrow(String fullName, BotMessage pmap, Exception ex) {
			ex.printStackTrace(); 
			return ex;
		}
	};

	public T onSuccess(String fullName, BotMessage pmap, Object result) ;
	public T onThrow(String fullName, BotMessage pmap, Exception ex) ;
}

