package net.ion.talk.let;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
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
        botManager = BotManager.create();
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
                .addParameter(Const.User.UserId, "fakeBot")
                .addParameter(Const.Message.Event, Const.Event.onInvited)
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
                .addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);

        tserver.mockClient().close();
        assertEquals(Status.SUCCESS_OK.getCode(), response.getStatus().getCode());
        assertEquals("{\"status\":\"OK\"}", response.getEntityAsText());

    }

    public void testNotFoundBot(){

        Response response = tserver.mockClient().fake().createRequest("/bot")
                .addParameter(Const.User.UserId, "notFoundBot")
                .addParameter(Const.Message.Event, Const.Event.onInvited)
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
                .addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);

        tserver.mockClient().close();
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
    }

    public void testInvalidEvent(){

        EmbedBot fakeBot = new FakeBot();
        botManager.registerBot(fakeBot);


        Response response = tserver.mockClient().fake().createRequest("/bot")
                .addParameter(Const.User.UserId, "fakeBot")
                .addParameter(Const.Message.Event, "invalidEvent")
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
                .addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);
        tserver.mockClient().close();
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
    }

    public void testInvalidParameter(){

        EmbedBot fakeBot = new FakeBot();
        botManager.registerBot(fakeBot);

        Response response = tserver.mockClient().fake().createRequest("/bot")
                .addParameter(Const.User.UserId, "fakeBot")
                .addParameter(Const.Message.Event, Const.Event.onInvited)
                .addParameter("Invalid", "Parameter").handle(Method.POST);

        tserver.mockClient().close();
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
    }


    private class FakeBot implements EmbedBot {
        @Override
        public String id() {
            return "fakeBot";
        }

        @Override
        public JsonObject onInvited(String roomId) {
            return null;
        }

        @Override
        public JsonObject onExit(String roomId) {
            return null;
        }

        @Override
        public JsonObject onUserEnter(String roomId, String userId) {
            return null;
        }

        @Override
        public JsonObject onUserExit(String roomId, String userId) {
            return null;
        }

        @Override
        public JsonObject onMessage(String roomId, String sender, String message) {
            return null;
        }
    }
}

