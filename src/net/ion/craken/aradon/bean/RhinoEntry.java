package net.ion.craken.aradon.bean;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.IService;
import net.ion.radon.core.context.OnEventObject;
import net.ion.script.rhino.ResponseHandler;
import net.ion.script.rhino.RhinoEngine;
import net.ion.script.rhino.RhinoScript;
import net.ion.talk.ParameterMap;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import org.mozilla.javascript.NativeJavaObject;

public class RhinoEntry implements OnEventObject {

	private RhinoEngine rengine;
	public final static String EntryName = "rengine";

	private RhinoEntry(RhinoEngine rengine) {
		this.rengine = rengine;
	}

	public final static RhinoEntry test() throws IOException {
		return new RhinoEntry(RhinoEngine.create());
	}

	@Override
	public void onEvent(AradonEvent event, IService service) {
		if (event == AradonEvent.START) {
			rengine.start();
		} else if (event == AradonEvent.STOP) {
			rengine.shutdown();
		}
	}

	public RhinoScript newScript(String sname) {
		return rengine.newScript(sname);
	}

    private String getValidatedScript(ReadSession rsession, String scriptPath){
        if(rsession.exists(scriptPath))
            return rsession.pathBy(scriptPath).property("script").stringValue();
        else
            throw new IllegalArgumentException("cannot found script in craken path:" + scriptPath);
    }

    public Object executePath(ReadSession rsession, String scriptId, String scriptPath, ParameterMap params){
        String script = getValidatedScript(rsession, scriptPath);
        return executeScript(rsession, scriptId, script, params);
    }

    public Object executeScript(ReadSession rsession, String scriptId, String script, ParameterMap params){

        if(StringUtil.isEmpty(scriptId))
            throw new IllegalArgumentException("Required messageId!");
        if(StringUtil.isEmpty(script))
            throw new IllegalArgumentException("Required script!");

        RhinoScript rscript = newScript(scriptId).defineScript(script);

        if (params == null)
            rscript.bind("params", ParameterMap.create(JsonObject.create()));
        else
            rscript.bind("params", params);

        return execute(rsession, rscript);
    }



    private Object execute(ReadSession rsession, RhinoScript rscript) {

        rscript.bind("session", rsession)
                .bind("rb", TalkResponseBuilder.create());

        return rscript.exec(new ResponseHandler<Object>() {
            @Override
            public Object onSuccess(RhinoScript script, Object rtnValue, long elapsedTime) {
                if(rtnValue instanceof NativeJavaObject)
                    return ((NativeJavaObject)rtnValue).unwrap();
                else
                    return rtnValue;
            }

            @Override
            public Object onFail(RhinoScript script, Throwable ex, long elapsedTime) {
                return ex;
            }
        });

    }

}
