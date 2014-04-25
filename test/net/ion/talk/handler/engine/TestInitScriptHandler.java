package net.ion.talk.handler.engine;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;
import net.ion.talk.TalkEngine;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 17.
 * Time: 오후 1:27
 * To change this template use File | Settings | File Templates.
 */
public class TestInitScriptHandler extends TestCase {

    private ReadSession rsession;
    private TalkEngine tengine;
    private InitScriptHandler handler;

    public void setUp() throws Exception {
        super.setUp();

        handler = new InitScriptHandler();
        tengine = TalkEngine.testCreate().registerHandler(handler);
        rsession = tengine.readSession();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/script/ryun/test").property("script", "test!!!");
                wsession.pathBy("/script/ryun/test/inner").property("script", "innerScript!");
                wsession.pathBy("/script/ryun/test/inner2").property("script", "innerScript2!");
                return null;
            }
        });


        tengine.startEngine();



    }

    public void tearDown() throws Exception {

        File dir = new File("./script/ryun");
        FileUtil.deleteDirectory(dir);

        tengine.stopEngine();
        super.tearDown();
    }


    public void testOnEngineStart() throws Exception {
        // tengine.startForTest(); -> Called onEngineStart() function at time setUp() method.


        final List<File> files = handler.getAllScriptFromFile("./script");

        for(File file : files){
            assertTrue(rsession.exists(StringUtil.replace(file.getPath().substring(1, file.getPath().lastIndexOf(".script")), "\\","/")));
        }
    }

    public void testCrakenToFile() throws Exception {



        List<ReadNode> nodes = handler.getAllChildrenFromCraken("/script");
        for(ReadNode node : nodes){
            String script = node.property("script").stringValue();

            Debug.line(node.parent().fqn().toString());
            File dir = new File("."+node.parent().fqn().toString());
            if(!dir.exists())
                dir.mkdirs();


            FileOutputStream fos = new FileOutputStream("."+node.fqn().toString()+".script");
            IOUtil.write(script, fos);
            fos.close();

            FileInputStream fis = new FileInputStream("." + node.fqn().toString()+".script");
            String stringFromFile = IOUtil.toStringWithClose(fis);
            assertEquals(script, stringFromFile);
        }
    }

    public void testFileToCraken() throws Exception {

        //prepare files.
        List<ReadNode> nodes = handler.getAllChildrenFromCraken("/script");
        for(ReadNode node : nodes){
            String script = node.property("script").stringValue();

            Debug.line(node.parent().fqn().toString());
            File dir = new File("."+node.parent().fqn().toString());
            if(!dir.exists())
                dir.mkdirs();

            FileOutputStream fos = new FileOutputStream("."+node.fqn().toString()+".script");
            IOUtil.write(script, fos);
            fos.close();
        }

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/script").removeSelf();
                return null;
            }
        });


        final List<File> files = handler.getAllScriptFromFile("./script");

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                for(File file : files){

                    FileInputStream fis = new FileInputStream(file);
                    String script = IOUtil.toStringWithClose(fis);
                    wsession.pathBy(file.getPath().substring(1, file.getPath().lastIndexOf(".script"))).property("script", script);
                }
                return null;
            }
        });

        for(File file : files){
            assertTrue(rsession.exists(file.getPath().substring(1, file.getPath().lastIndexOf(".script"))));
        }
    }

    public void testAllchildren() throws Exception {

        List<ReadNode> nodes = handler.getAllChildrenFromCraken("/script");
        assertTrue(nodes.contains(rsession.pathBy("/script/ryun/test")));
        assertTrue(nodes.contains(rsession.pathBy("/script/ryun/test/inner")));
        assertTrue(nodes.contains(rsession.pathBy("/script/ryun/test/inner2")));

    }

}