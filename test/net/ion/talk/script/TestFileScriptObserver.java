package net.ion.talk.script;

import junit.framework.TestCase;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Author: Ryunhee Han
 * Date: 2014. 4. 20.
 */
public class TestFileScriptObserver extends TestCase{

    private ExecutorService es = Executors.newCachedThreadPool();

    public void testFirst() throws Exception {
        ScriptManager manager = ScriptManager.create(es);

        FileScriptObserver observer = FileScriptObserver.create(es, true, 500);
        manager.addObserver(observer);
        manager.start();

        File path = new File("./script/");
        observer.addPath(path, false);

        es.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(manager.getScript("test@ryun") instanceof ScriptObject);

    }

}
