package net.ion.talk.script;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Author: Ryunhee Han
 * Date: 2014. 4. 20.
 */
public class TestFileScriptObserver extends TestCase{

    private ExecutorService es = Executors.newScheduledThreadPool(1);
    private ScriptManager manager;
    private FileScriptObserver observer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        manager = ScriptManager.create(es);

        observer = FileScriptObserver.create(es, true, 500);
        manager.addObserver(observer);
        manager.start();

        File path = new File("./script/");
        observer.addPath(path, false);

        createTestScript();
        es.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals("testScript", manager.getScript("test@testScript").name());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        removeTestScript();
    }

    public void testReadScript() throws Exception {
        assertEquals("var test = 1;", manager.getScript("test@testScript").script());
    }

    public void testRemoveScript() throws Exception {
        removeTestScript();
        es.awaitTermination(1, TimeUnit.SECONDS);
        assertFalse(manager.ifExistScript("test@testScript"));
    }

    public void testChangedScript() throws Exception {
        changeTestScript();
        es.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals("var test = 1234;", manager.getScript("test@testScript").script());
    }

    private void removeTestScript() {
        File testScript = new File("./script/test.script");
        testScript.delete();
    }

    private void createTestScript() throws IOException {
        String script = "{\"scripts\":[{\"name\":\"testScript\",\"script\":\"var test = 1;\"}]}";

        FileOutputStream fos = new FileOutputStream("./script/test.script");
        fos.write(script.getBytes());
        IOUtil.closeQuietly(fos);
    }

    private void changeTestScript() throws IOException {

        String script = "{\"scripts\":[{\"name\":\"testScript\",\"script\":\"var test = 1234;\"}]}";

        FileOutputStream fos = new FileOutputStream("./script/test.script");
        fos.write(script.getBytes());
        IOUtil.closeQuietly(fos);
    }


}
