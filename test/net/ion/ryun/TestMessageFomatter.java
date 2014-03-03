package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 26. Time: 오후 5:21 To change this template use File | Settings | File Templates.
 */
public class TestMessageFomatter extends TestCase {

	public void testFirst() throws Exception {
		String string = MessageFormat.format("var memberList = session.pathBy(''/rooms/{0}/members'').childrenNames().toArray();\n" + "\n" + "session.tranSync(function(wsession)'{var messageNode = wsession.pathBy(''/rooms/{0}/messages/{2}'').property(''message'', ''Hello! {3}'')\n"
				+ "\t.property(''sender'', ''{1}'')\n" + "\t.property(''clientScript'', ''client.room().message(args.message)'')\n" + "\t.property(''requestId'', ''{2}'')\n" + "\n" + "\tfor(i in memberList){\n" + "\t\tif(memberList[i] != ''{1}'')\n"
				+ "\t\t\tmessageNode.append(''receivers'', memberList[i]);\n" + "\t}\n" + "'});", new Object[] { "1234", "echobot", new ObjectId().toString(), "echoBot" });

		// string = MessageFormat.format("{0}{1}", "hello", "bye");
		Debug.line(string);
	}

	public void testTwo() throws Exception {
		String string = String.format("var memberList = session.pathBy('/rooms/%s/members').childrenNames().toArray();\n" + "\n" + "session.tranSync(function(wsession){\n" + "\tvar messageNode = wsession.pathBy('/rooms/%s/messages/%s')\n" + "\t.property('message', 'bye! %s')\n"
				+ "\t.property('sender', '%s')\n" + "\t.property('clientScript', 'client.room().message(args.message)')\n" + "\t.property('requestId', '%s')\n" + "\n" + "\tfor(i in memberList){\n" + "\t\tif(memberList[i] != '%s')\n" + "\t\t\tmessageNode.append('receivers', memberList[i]);\n"
				+ "\t}\n" + "});", "1234", "1234", "messageId", "userId", "sender", "requestId", "userId");

		Debug.line(string);
	}
}
