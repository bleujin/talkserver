package net.ion.talk.let;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

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
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;
import net.ion.talk.bean.Const;
import net.ion.talk.handler.template.DummyPath;

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
		StubServer ss = StubServer.create(SVGLet.class) ;
		ss.treeContext().putAttribute("repository", this.r) ;
		
		StubHttpResponse response = ss.request("/svg/old/roomroom/sender/12345.svg?charId=bat").get() ;
		Debug.line(response.contentsString()) ;
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
    

	public void testURI() throws Exception {
		URI uri = URI.create("/svg/command/0?message=Welcome%20to%20ToonTalk%20Mobile&type=receiver");
		URI encodedURI = URI.create("/svg/command/0?" + URLEncoder.encode("message=Welcome to ToonTalk Mobile&type=receiver"));
		
		Debug.line(encodedURI.getQuery()) ;
	}
	
	
	public void testGetWelcome() throws Exception {
		StubServer ss = StubServer.create(SVGLet.class) ;
		StubHttpResponse response = ss.request("/svg/command/0?message=Welcome%20to%20ToonTalk%20Mobile&type=receiver").get() ;
		
		assertEquals("image/svg+xml", response.header(HttpHeaders.CONTENT_TYPE)) ;
		assertTrue(response.contentsString().startsWith("<svg")) ;
	}
}
