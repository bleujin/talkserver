package net.ion.talk.handler.engine;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.craken.tree.PropertyId;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.talk.TalkEngine;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.handler.TalkHandler;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 17.
 * Time: 오후 2:18
 * To change this template use File | Settings | File Templates.
 */
public class InitScriptHandler implements TalkHandler{

    private static FileFilter SCRIPT = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".script") && pathname.isFile();
        }
    };

    private static FileFilter DIRECTORY = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };


    private ReadSession rsession;

    @Override
    public TalkEngine.Reason onConnected(TalkEngine tengine, UserConnection uconn) {
        return TalkEngine.Reason.OK;
    }

    @Override
    public void onClose(TalkEngine tengine, UserConnection uconn) {
    }

    @Override
    public void onMessage(TalkEngine tengine, UserConnection uconn, ReadSession rsession, TalkMessage tmsg) {
    }

    @Override
    public void onEngineStart(TalkEngine tengine) throws Exception {
        rsession = tengine.readSession();


        final List<File> files = getAllScriptFromFile("./script");

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                for(File file : files){

                    FileInputStream fis = new FileInputStream(file);
                    String script = IOUtil.toStringWithClose(fis);
                    Debug.line("Script Loaded:" + FilenameUtils.separatorsToUnix(file.getPath()));
                    wsession.pathBy(FilenameUtils.separatorsToUnix(file.getPath().substring(1, file.getPath().lastIndexOf(".script")))).property("script", script);
                }
                return null;
            }
        });
    }

    @Override
    public void onEngineStop(TalkEngine tengine) {
    }



    protected List<ReadNode> getAllChildrenFromCraken(String path) {

        List<ReadNode> nodes = ListUtil.newList();

        ReadChildren children = rsession.pathBy(path).children();
        Iterator<ReadNode> iter = children.iterator();
        while (iter.hasNext()) {
            ReadNode child = iter.next();
            if(child.hasProperty(PropertyId.fromIdString("script")))
                nodes.add(child);
            nodes.addAll(getAllChildrenFromCraken(child.fqn().toString()));
        }
        return nodes;
    }

    public List<File> getAllScriptFromFile(String path) throws IOException {

        List<File> scripts = ListUtil.newList();
        File dir = new File(path);

        File[] subDirs = dir.listFiles(DIRECTORY);
        File[] files = dir.listFiles(SCRIPT);

        if(subDirs!=null && files!=null){
            Collections.addAll(scripts, files);
            for(File subDir : subDirs){
                scripts.addAll(getAllScriptFromFile(subDir.getPath()));
            }
        }

        return scripts;
    }
}

























