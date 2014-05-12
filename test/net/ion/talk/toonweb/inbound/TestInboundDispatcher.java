package net.ion.talk.toonweb.inbound;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.ion.framework.util.HashFunction;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.stub.StubConnection;


public class TestInboundDispatcher extends TestCase {
	
	
    public void testThrowsExceptionWithStacktrace() throws Throwable {
        String clientException = "" +
                "{\"action\":\"__reportClientException\"," +
                "\"args\":[\"The error message\",[" +
                "" +
                "\"Object.stringify (native)\"," +
                "\"Object.say (http://bleujin.com:9877/chatroom.js:50:22)\"," +
                "\"invokeOnTarget (http://bleujin.com:9877/easyremote.js:68:28)\"," +
                "\"{anonymous}()@http://bleujin.com:9877/easyremote.js:98:21\"," +
                "\"jsonParser (http://bleujin.com:9877/easyremote.js:34:9)\"," +
                "\"WebSocket.<anonymous> (http://bleujin.com:9877/easyremote.js:93:9)" +
                "" +
                "\"]]}";
        InboundDispatcher dispatcher = new JsonInboundDispatcher(this, SomeClient.class);
        WebSocketConnection connection = new StubConnection();
        try {
            dispatcher.dispatch(connection, clientException, this);
            fail();
        } catch (JavaScriptException e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            String expected = "" +
                    "net.ion.talk.toonweb.inbound.JavaScriptException: The error message\n" +
                    "\tat Object.stringify(native)\n" +
                    "\tat Object.say(http://bleujin.com:9877/chatroom.js:50:22)\n" +
                    "\tat .invokeOnTarget(http://bleujin.com:9877/easyremote.js:68:28)\n" +
                    "\tat .anonymous(http://bleujin.com:9877/easyremote.js:98:21)\n" +
                    "\tat .jsonParser(http://bleujin.com:9877/easyremote.js:34:9)\n" +
                    "\tat WebSocket.<anonymous>(http://bleujin.com:9877/easyremote.js:93:9)\n" +
                    "";
            assertEquals(StringUtil.deleteWhitespace(expected), StringUtil.deleteWhitespace(trace.toString()));
        }
    }

	public void testCreatesTraceForRegularLine() {
		assertStackTrace("Object.say (http://bleujin.com:9877/chatroom.js:50:22)", "Object", "say", "http://bleujin.com:9877/chatroom.js:50", 22);
	}

	private void assertStackTrace(String jsLine, String className, String methodName, String fileName, int line) {
		StackTraceElement e = JavaScriptException.newStackTraceElement(jsLine);
		assertEquals(line, e.getLineNumber());
		assertEquals(fileName, e.getFileName());
		assertEquals(className, e.getClassName());
		assertEquals(methodName, e.getMethodName());
	}

	private interface SomeClient {
	}
}
