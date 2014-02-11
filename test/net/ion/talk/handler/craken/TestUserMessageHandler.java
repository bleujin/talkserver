package net.ion.talk.handler.craken;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:43
 * To change this template use File | Settings | File Templates.
 */
public class TestUserMessageHandler extends TestCrakenHandlerBase{

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rsession.workspace().cddm().add(new TalkMessageHandler());
    }

    public void testSendMessage() throws Exception {

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/connections/ryun").property("server", wsession.workspace().repository().memberId());

                wsession.pathBy("/rooms/1/members/ryun");
                wsession.pathBy("/rooms/1/members/alex");

                wsession.pathBy("/rooms/1/messages/1")
                    .property("message", "Hello World!")
                    .property("state", "smile")
                    .refTo("sender", "/users/alex")
                    .refTo("character", "/chars/1");
                return null;
            }
        });


        Thread.sleep(100);

        ReadNode notiRyun = rsession.pathBy("/notifies/ryun").children().next();
        assertEquals("Hello World!", notiRyun.ref("message").property("message").stringValue());
        assertEquals("smile", notiRyun.ref("message").property("state").stringValue());
        assertEquals(rsession.ghostBy("/users/alex"), notiRyun.ref("message").ref("sender"));
        assertEquals(rsession.ghostBy("/chars/1"), notiRyun.ref("message").ref("character"));

        ReadNode notiAlex = rsession.pathBy("/notifies/alex").children().next();
        assertEquals("Hello World!", notiAlex.ref("message").property("message").stringValue());
        assertEquals("smile", notiAlex.ref("message").property("state").stringValue());
        assertEquals(rsession.ghostBy("/users/alex"), notiAlex.ref("message").ref("sender"));
        assertEquals(rsession.ghostBy("/chars/1"), notiAlex.ref("message").ref("character"));
    }
}
