package net.ion.talk.handler.craken;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.talk.ToonServer;
import net.ion.talk.bean.Const;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 4.
 * Time: 오후 2:43
 * To change this template use File | Settings | File Templates.
 */
public class TestUserMessageHandler extends TestCrakenHandlerBase{

    private String memberId;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rsession.workspace().cddm().add(new TalkMessageHandler());
        memberId = rsession.workspace().repository().memberId();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/notifies/bleujin");
                wsession.pathBy("/notifies/ryun");
                wsession.pathBy("/rooms/1234/members").addChild("ryun");
                wsession.pathBy("/rooms/1234/members").addChild("bleujin");
                return null;
            }
        });
    }

    public void testSendMessage() throws Exception {

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/connections/ryun").property("server", wsession.workspace().repository().memberId());

                wsession.pathBy("/rooms/1/members/ryun");
                wsession.pathBy("/rooms/1/members/alex");

                wsession.pathBy("/rooms/1/messages/1")
                    .property(Const.Message.Message, "Hello World!")
                    .refTo("sender", "/users/alex")
                    .refTo("character", "/chars/1");
                return null;
            }
        });


        Thread.sleep(1000);



        ReadNode notiRyun = rsession.pathBy("/notifies/ryun").children().next();
        assertEquals("Hello World!", notiRyun.ref("message").property("message").stringValue());
        assertEquals(rsession.ghostBy("/users/alex"), notiRyun.ref("message").ref("sender"));
        assertEquals(rsession.ghostBy("/chars/1"), notiRyun.ref("message").ref("character"));

        ReadNode notiAlex = rsession.pathBy("/notifies/alex").children().next();
        assertEquals("Hello World!", notiAlex.ref("message").property("message").stringValue());
        assertEquals(rsession.ghostBy("/users/alex"), notiAlex.ref("message").ref("sender"));
        assertEquals(rsession.ghostBy("/chars/1"), notiAlex.ref("message").ref("character"));
    }

    public void testSpecificReceiver() throws Exception {

        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/rooms/1234/messages/testMessage").append(Const.Message.Receivers, "ryun").property(Const.Message.Message, "Hello Ryun");

                return null;
            }
        });

        Thread.sleep(1000);
        assertEquals(1, rsession.pathBy("/notifies/ryun/").children().toList().size());
        assertEquals(0, rsession.pathBy("/notifies/bleujin/").children().toList().size());

    }

    public void testAllReceivers() throws Exception {

        rsession.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {

                wsession.pathBy("/rooms/1234/messages/testMessage").property(Const.Message.Message, "Hello Everybody~");

                return null;
            }
        });

        Thread.sleep(1000);
        assertEquals(1, rsession.pathBy("/notifies/ryun/").children().toList().size());
        assertEquals(1, rsession.pathBy("/notifies/bleujin/").children().toList().size());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
