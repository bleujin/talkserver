package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.util.AradonTester;
import net.ion.talk.bean.Const;
import net.ion.talk.handler.template.DummyPath;
import org.restlet.Response;
import org.restlet.data.Method;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class TestMessageSVGLet extends TestCase {

	private RepositoryEntry r;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.r = RepositoryEntry.test() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		this.r.shutdown() ;
		super.tearDown();
	}
	
	
	public void testGet() throws Exception {
		
		Aradon aradon = AradonTester.create().register("svg", "/message/{roomId}/{type}/{messageId}.svg", OldSVGLet.class).getAradon() ;
		aradon.getServiceContext().putAttribute("repository", this.r) ;
		
		AradonClient ac = AradonClientFactory.create(aradon) ;
		Response response = ac.createRequest("/svg/message/roomroom/sender/12345.svg?charId=bat").handle(Method.GET) ;
	
		Debug.line(response.getEntityAsText()) ;
	}

    public void testMapping() throws Exception {

        ReadSession session = r.login();
        session.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/test/message").property("time", new Date().getTime()).property(Const.Message.MessageId, "abcd").property(Const.Message.Message, "hihihi").refTo("sender", "/test/airkjh");
                wsession.pathBy("/test/airkjh").property("nickname", "airkjh");
                return null;
            }
        });



        Engine engine = session.workspace().parseEngine();
        String template = IOUtil.toStringWithClose(DummyPath.class.getResourceAsStream("default_sender.tpl")) ;

        ReadNode messageNode = session.pathBy("/test/message");
        Map<String, Object> map = MapUtil.chainKeyMap().put("node", messageNode).toMap();

        Debug.line(messageNode.ref("sender").property("nickname").asString());

        String result = engine.transform(template, map) ;
        Debug.line(result);

    }
}
