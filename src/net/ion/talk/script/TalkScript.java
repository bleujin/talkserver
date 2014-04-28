package net.ion.talk.script;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.script.CrakenScript;
import net.ion.craken.script.FileAlterationMonitor;
import net.ion.craken.script.JsonBuilder;
import net.ion.framework.db.Rows;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.talk.ParameterMap;
import net.ion.talk.TalkMessage;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

import sun.org.mozilla.javascript.internal.NativeObject;

public class TalkScript {

	private ScriptEngine sengine;
	private Map<String, Object> packages = MapUtil.newCaseInsensitiveMap();
	private FileAlterationMonitor monitor;
	private ScheduledExecutorService ses;
	private ReadSession rsession;

	private TalkScript(ReadSession rsession, ScheduledExecutorService ses) {
		ScriptEngineManager manager = new ScriptEngineManager();
		this.ses = ses ;
		this.sengine = manager.getEngineByName("JavaScript");
		sengine.put("session", rsession);
		sengine.put("rb", TalkResponseBuilder.create()) ;
		this.rsession = rsession ;
	}

	public static TalkScript create(ReadSession rsession, ScheduledExecutorService ses) {
		return new TalkScript(rsession, ses);
	}

	public TalkScript readDir(final File scriptDir) throws IOException {
		return readDir(scriptDir, false) ;
	}
	
	public ReadSession session(){
		return rsession ;
	}
	
	public TalkScript readDir(final File scriptDir, boolean reloadWhenDetected) throws IOException {
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
				Debug.line("Package Deleted", file);
				packages.remove(FilenameUtils.getBaseName(file.getName())) ;
			}
			
			@Override
			public void onFileCreate(File file) {
				Debug.line("Package Created", file);
				loadPackageScript(file) ;
			}
			
			@Override
			public void onFileChange(File file) {
				Debug.line("Package Changed", file);
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

	public TalkScript addScript(String packName, String script) throws ScriptException{
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
				result.add(pkgName + "/" + fnName) ;
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

	
	public <T> T callFn(String fullFnName, ParameterMap params, ScriptSuccessHandler<T> shandler, ScriptExceptionHandler<T> ehander) {
		try {
			
			String[] names = StringUtil.split(fullFnName, '/') ;
			
			String packName = names[0];
			String fnName = names[1];

			Object pack = packages.get(packName);
			if (pack == null) return ehander.ehandle(new IOException("not found package : " + fullFnName) , fullFnName, params) ;

			Object result = ((Invocable) sengine).invokeMethod(pack, fnName, params);
			if(result instanceof NativeJavaObject) result = ((NativeJavaObject)result).unwrap() ;  
			result = ObjectUtil.coalesce(result, "undefined") ;
			  
			return shandler.success(result);
		} catch (ScriptException ex) {
			return ehander.ehandle(ex, fullFnName, params) ;
		} catch (IndexOutOfBoundsException ex) {
			return ehander.ehandle(ex, fullFnName, params) ;
		} catch (NoSuchMethodException ex) {
			return ehander.ehandle(ex, fullFnName, params) ;
		}
		
	}

	public Object callFn(String fullFnName,  ParameterMap params) throws IOException{
		return callFn(fullFnName, params,  ScriptSuccessHandler.ReturnNative, ScriptExceptionHandler.ReturnExceptionHandler) ;
	}
}
