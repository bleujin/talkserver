package net.ion.talk.let;

import net.ion.craken.node.ReadSession;
import net.ion.talk.bean.Const;
import net.ion.talk.bot.EmbedBot;
import net.ion.talk.bot.BotManager;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 5:16
 * To change this template use File | Settings | File Templates.
 */
public class TestEmbedBotLet extends TestBaseLet {

    private BotManager botManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tserver.startRadon();
        botManager = BotManager.create(tserver.readSession());
        tserver.talkEngine().context().putAttribute(BotManager.class.getCanonicalName(), botManager);
    }

    @Override
    public void tearDown() throws Exception {
        tserver.stop();
        super.tearDown();
    }

    public void testFakeBot() throws Exception {

        EmbedBot fakeBot = new FakeBot();
        botManager.registerBot(fakeBot);



        Response response = tserver.mockClient().fake().createRequest("/bot")
                .addParameter(Const.Bot.BotId, "fakeBot")
                .addParameter(Const.Message.Event, Const.Event.onEnter)
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
                .addParameter(Const.Message.MessageId, "testMessage")
                .addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);

        tserver.mockClient().close();
        assertEquals(Status.SUCCESS_OK.getCode(), response.getStatus().getCode());

    }

    public void testNotFoundBot(){

        Response response = tserver.mockClient().fake().createRequest("/bot")
                .addParameter(Const.Bot.BotId, "notFoundBot")
                .addParameter(Const.Message.Event, Const.Event.onEnter)
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
                .addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);

        tserver.mockClient().close();
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
    }

    public void testInvalidEvent() throws Exception {

        EmbedBot fakeBot = new FakeBot();
        botManager.registerBot(fakeBot);


        Response response = tserver.mockClient().fake().createRequest("/bot")
                .addParameter(Const.Bot.BotId, "fakeBot")
                .addParameter(Const.Message.Event, "invalidEvent")
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
                .addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);
        tserver.mockClient().close();
//        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
//        assertEquals("suceess", JsonObject.fromString(response.getEntityAsText()).asString("status"));
    }

    public void testInvalidParameter() throws Exception {

        EmbedBot fakeBot = new FakeBot();
        botManager.registerBot(fakeBot);

        Response response = tserver.mockClient().fake().createRequest("/bot")
                .addParameter(Const.Bot.BotId, "fakeBot")
                .addParameter(Const.Message.Event, Const.Event.onEnter)
                .addParameter("Invalid", "Parameter").handle(Method.POST);

        tserver.mockClient().close();
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
    }


    private class FakeBot extends EmbedBot {

        protected FakeBot() {
            super("fakeBot", "http://localhost:9000/bot", null);
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String requestURL() {
            return requestURL;
        }

        @Override
        public void onEnter(String roomId, String userId) {
        }

        @Override
        public void onExit(String roomId, String userId) {
        }

        @Override
        public void onMessage(String roomId, String sender, String message) {
        }

        @Override
        public void onFilter(String roomId, String sender, String message, String messageId) throws Exception {
        }
    }
}

