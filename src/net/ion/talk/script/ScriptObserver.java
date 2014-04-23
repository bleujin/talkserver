package net.ion.talk.script;

/**
 * Author: Ryunhee Han
 * Date: 2014. 4. 20.
 */
public abstract class ScriptObserver {

    protected ScriptManager manager;

    protected void setManager(ScriptManager manager){
        this.manager = manager;
    }


    protected abstract void start() throws Exception;
    protected abstract void stop() throws Exception;

}
