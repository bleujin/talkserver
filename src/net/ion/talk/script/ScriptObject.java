package net.ion.talk.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

/**
 * Author: Ryunhee Han
 * Date: 2014. 4. 21.
 */
public class ScriptObject {


    private String script;
    private String name;
    private Script compiledScript;

    public ScriptObject(String name, String script) {
        this.name = name;
        this.script = script;
    }

    public void compile() {
        Context context = Context.enter();
        compiledScript = context.compileString(script, name, 1, null);
        Context.exit();
    }

    public Script compiledScript(){
        if(compiledScript==null) compile();
        return compiledScript;
    }

    public String name(){
        return name;
    }

    public String script(){
        return script;
    }

    public static ScriptObject create(String name, String script) {
        return new ScriptObject(name, script);
    }


}
