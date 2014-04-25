package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.nradon.Radon;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.talk.ToonServer;
import net.ion.talk.bean.Const;
import net.ion.talk.bot.BotManager;
import net.ion.talk.bot.EmbedBot;

import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 19. Time: 오후 5:16 To change this template use File | Settings | File Templates.
 */
public class TestEmbedBotLet extends TestCase {

	private BotManager botManager;
	private Radon radon;
	private AradonClient fake;
	private RepositoryEntry repoEntry;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.repoEntry = RepositoryEntry.test();
		botManager = BotManager.create(repoEntry.login());
		Configuration configuration = ConfigurationBuilder.newBuilder()	
			.aradon()
				.addAttribute(BotManager.class.getCanonicalName(), botManager)
			.sections()
				.restSection("bot")
					.path("bot").addUrlPattern("/{botId}").matchMode(IMatchMode.STARTWITH).handler(EmbedBotLet.class).build() ;
		Aradon aradon = Aradon.create(configuration);
		this.fake = AradonClientFactory.create(aradon) ;
		this.radon = aradon.toRadon() ;
	}

	@Override
	public void tearDown() throws Exception {
		repoEntry.shutdown(); 
		fake.stop(); 
		radon.stop();
		super.tearDown();
	}

	public void testFakeBot() throws Exception {

		EmbedBot fakeBot = new FakeBot();
		botManager.registerBot(fakeBot);

		Response response = fake.createRequest("/bot/fakeBot")
                .addParameter(Const.Message.Event, Const.Event.onEnter)
                .addParameter(Const.Message.MessageId, "hallo")
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
				.addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);

		assertEquals(Status.SUCCESS_OK.getCode(), response.getStatus().getCode());

	}

	public void testNotFoundBot() throws Exception {

		Response response = fake.createRequest("/bot/notFoundBot")
                .addParameter(Const.Message.Event, Const.Event.onEnter)
                .addParameter(Const.Message.MessageId, "hallo")
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
				.addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);

		assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
	}

	public void testInvalidEvent() throws Exception {

		EmbedBot fakeBot = new FakeBot();
		botManager.registerBot(fakeBot);

		Response response = fake.createRequest("/bot/fakeBot")
                .addParameter(Const.Message.Event, "invalidEvent")
                .addParameter(Const.Message.MessageId, "hallo")
                .addParameter(Const.Message.Sender, "ryuneeee")
                .addParameter(Const.Room.RoomId, "1")
				.addParameter(Const.Message.Message, "HelloWorld!").handle(Method.POST);
		// assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
		// assertEquals("suceess", JsonObject.fromString(response.getEntityAsText()).asString("status"));
	}

	public void testInvalidParameter() throws Exception {

		EmbedBot fakeBot = new FakeBot();
		botManager.registerBot(fakeBot);

		Response response = fake.createRequest("/bot/fakeBot")
                .addParameter(Const.Message.Event, Const.Event.onEnter)
                .addParameter("Invalid", "Parameter").handle(Method.POST);

		assertEquals(Status.CLIENT_ERROR_BAD_REQUEST.getCode(), response.getStatus().getCode());
	}

	private class FakeBot extends EmbedBot {

        protected FakeBot() {
            super("fakeBot", "fakeBot", "페이크봇", "http://localhost:9000/bot", null);
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
        public boolean isSyncBot() {
            return false;
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
