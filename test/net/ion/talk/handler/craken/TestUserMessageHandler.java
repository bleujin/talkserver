package net.ion.talk.handler.craken;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;

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
        rsession.workspace().cddm().add(new UserUserMessageHandler());
    }

    public void testSendMessage() throws Exception {

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/rooms/1/members/ryun");
                wsession.pathBy("/rooms/1/members/alex");

                wsession.pathBy("/rooms/1/members/ryun/messages/1")
                    .property("message", "Hello World!")
                    .property("state", "smile")
                    .refTo("sender", "/users/alex")
                    .refTo("character", "/chars/1");
                return null;
            }
        });


        Thread.sleep(100);

        ReadNode notiRyun = rsession.pathBy("notifies/ryun/1").ref("message");
        assertEquals("Hello World!", notiRyun.property("message").stringValue());
        assertEquals("smile", notiRyun.property("state").stringValue());
        assertEquals(rsession.ghostBy("/users/alex"), notiRyun.ref("sender"));
        assertEquals(rsession.ghostBy("/chars/1"), notiRyun.ref("character"));

        ReadNode notiAlex = rsession.pathBy("notifies/alex/1").ref("message");
        assertEquals("Hello World!", notiAlex.property("message").stringValue());
        assertEquals("smile", notiAlex.property("state").stringValue());
        assertEquals(rsession.ghostBy("/users/alex"), notiAlex.ref("sender"));
        assertEquals(rsession.ghostBy("/chars/1"), notiAlex.ref("character"));
    }
}
