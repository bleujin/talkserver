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
    private ReadSession rsession;
	public final static String EntryName = "rengine";

	private RhinoEntry(RhinoEngine rengine, ReadSession rsession) {
		this.rengine = rengine;
        this.rsession = rsession;
	}

	public final static RhinoEntry test(ReadSession rsession) throws IOException {
		return new RhinoEntry(RhinoEngine.create(), rsession);
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

    private String getValidatedScript(String scriptPath){
        if(rsession.exists(scriptPath))
            return rsession.pathBy(scriptPath).property("script").stringValue();
        else
            throw new IllegalArgumentException("cannot found script in craken path:" + scriptPath);
    }

    public Object executePath(String scriptId, String scriptPath, ParameterMap params){
        try {
            String script = getValidatedScript(scriptPath);
            return executeScript(scriptId, script, params);
        }  catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            return TalkResponseBuilder.makeResponse(e1);

        } catch (UnsupportedOperationException e2){
            e2.printStackTrace();
            return TalkResponseBuilder.makeResponse(e2);
        }
    }

    public Object executeScript(String scriptId, String script, ParameterMap params){

        if(StringUtil.isEmpty(scriptId))
            throw new IllegalArgumentException("Required messageId!");
        if(StringUtil.isEmpty(script))
            throw new IllegalArgumentException("Required script!");

        RhinoScript rscript = newScript(scriptId).defineScript(script);

        if (params == null)
            rscript.bind("params", ParameterMap.create(JsonObject.create()));
        else
            rscript.bind("params", params);

        return execute(rscript);
    }



    private Object execute(RhinoScript rscript) {

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
