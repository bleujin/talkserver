package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.client.AradonClient;
import net.ion.radon.core.EnumClass.IMatchMode;
import org.restlet.data.Method;

import java.net.InetAddress;

public class TestScriptExecLet extends TestBaseLet {

    private ReadSession session;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tserver.cbuilder().aradon()
                .sections().restSection("execute")
                .path("execute").addUrlPattern("/{path}.{format}").matchMode(IMatchMode.STARTWITH).handler(ScriptExecLet.class)
                .build();
        tserver.startRadon();
        session = tserver.readSession();
    }


    public void testAjaxScript() throws Exception {
        NewClient client = tserver.mockClient().real();
        String script = "rb.createBasic().property('name','ryun').build().toString();";
        RequestBuilder requestBuilder = new RequestBuilder()
                .setMethod(Method.POST)
                .addParameter("script", script);

        Request request = requestBuilder.setUrl("http://" + tserver.getHostAddress() + ":9000/execute/ajax.json").build();

        Response response = client.executeRequest(request).get();
        assertEquals("application/json; charset=UTF-8",response.getContentType());
        assertEquals("{\"name\":\"ryun\"}", response.getTextBody());

        request = requestBuilder.setUrl("http://" + tserver.getHostAddress() + ":9000/execute/ajax.string").build();

        response = client.executeRequest(request).get();
        assertEquals("text/plain; charset=UTF-8",response.getContentType());
        assertEquals("{\"name\":\"ryun\"}", response.getTextBody());
        client.close();
    }
}
