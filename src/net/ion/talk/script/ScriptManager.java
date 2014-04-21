package net.ion.talk.script;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Author: Ryunhee Han
 * Date: 2014. 4. 20.
 */
public class ScriptManager {
    private ExecutorService es;
    private List<ScriptObserver> observers;
    private Map<String, ScriptObject> scripts;
    private boolean started;

    public ScriptManager(ExecutorService es) {
        this.es = es;
        scripts = MapUtil.newMap();
        observers = ListUtil.newList();
    }

    public static ScriptManager create(ExecutorService es) {
        return new ScriptManager(es);
    }


    public void start() throws Exception {
        for(ScriptObserver observer : observers){
            observer.start();
        }

        started = true;
    }

    public void stop() throws Exception {
        for(ScriptObserver observer : observers){
            observer.stop();
        }

        started = false;
    }

    public void putScript(final String key, final ScriptObject sObj){
        es.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                sObj.compile();
                scripts.put(key, sObj);
                return null;
            }
        });
    }

    public ScriptObject getScript(String key){
        if(scripts.get(key) == null) throw new IllegalArgumentException("Can't find " + key);
        return scripts.get(key);
    }

    public void removeScript(String key){
        scripts.remove(key);
    }

    public List<ScriptObserver> getObservers(){
        return Collections.unmodifiableList(observers);
    }

    public ScriptManager removeObserver(ScriptObserver observer) throws Exception {
        if(started) observer.stop();
        observers.remove(observer);
        return this;
    }

    public ScriptManager addObserver(ScriptObserver observer) throws Exception {
        observer.setManager(this);
        if(started) observer.start();
        observers.add(observer);
        return this;
    }

    public Map<String, ScriptObject> getAllScript() {
        return Collections.unmodifiableMap(scripts);
    }

    public boolean ifExistScript(String key) {
        return scripts.containsKey(key);
    }
}
