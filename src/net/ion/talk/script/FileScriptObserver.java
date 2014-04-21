package net.ion.talk.script;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Author: Ryunhee Han
 * Date: 2014. 4. 20.
 */
public class FileScriptObserver extends ScriptObserver {

    private final ExecutorService es;
    private boolean reloadIfChanged;
    private FileAlterationMonitor monitor;

    private FileScriptObserver(ExecutorService es, boolean reloadIfChanged, long interval) throws Exception {
        this.es = es;
        this.reloadIfChanged = reloadIfChanged;
        this.monitor = new FileAlterationMonitor(interval);
    }


    @Override
    protected void start() throws Exception {
        monitor.start();
    }

    @Override
    protected void stop() throws Exception {
        monitor.stop();

    }

    public static FileScriptObserver create(ExecutorService es, boolean reloadIfChanged, int interval) throws Exception {
        return new FileScriptObserver(es, reloadIfChanged, interval);
    }

    public FileScriptObserver addPath(File path, boolean initialize, IOFileFilter filter) throws Exception {

        FileAlterationObserver observer = new FileAlterationObserver(path, filter);
        observer.addListener(new FileScriptListener());
        if(initialize) observer.initialize();
        monitor.addObserver(observer);

        return this;
    }

    public FileScriptObserver addPath(File path, boolean initialize) throws Exception {
        return addPath(path, initialize, null);
    }


    class FileScriptListener extends FileAlterationListenerAdaptor {
        @Override
        public void onFileCreate(final File file) {
            es.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {

                    String fileContent = null;
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        InputStreamReader ir = new InputStreamReader(fis, Charset.forName("utf-8"));
                        fileContent = IOUtil.toStringWithClose(ir);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JsonObject scripts = JsonObject.fromString(fileContent);

                    for(JsonElement element: scripts.getAsJsonArray("scripts")){
                        String scriptName = element.getAsJsonObject().asString("name");
                        String script = element.getAsJsonObject().asString("script");
                        ScriptObject sObj = ScriptObject.create(scriptName, script);

                        String scriptKey = FilenameUtils.getBaseName(file.getName()) +  "@" + scriptName;
                        manager.putScript(scriptKey, sObj);

                    }

                    return null;
                }
            });

        }

        @Override
        public void onFileChange(File file) {
            if (!FileScriptObserver.this.reloadIfChanged) return;
            onFileCreate(file);
        }

        @Override
        public void onFileDelete(final File file) {

            es.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {

                    String scriptKey = FilenameUtils.getBaseName(file.getName()) +  "@";

                    for(String key : manager.getAllScript().keySet()){
                        if(StringUtils.startsWith(key, scriptKey)) manager.removeScript(key);

                    }
                    return null;
                }
            });
        }
    }
}
