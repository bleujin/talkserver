package net.ion.talk.bot.connect;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.bean.Const;
import net.ion.talk.script.BotMessage;

public class TestBotClient extends TestCase {

	public void testFirst() throws IOException, InterruptedException, ExecutionException {

		RepositoryImpl repo = RepositoryImpl.inmemoryCreateWithTest();
		ReadSession session = repo.login("test");

		BotMessage bm = BotMessage.create().botId("simsimi").clientScript(Const.Message.DefaultOnMessageClientScript).message("Hello").sender("simsimi");

		NewClient nc = NewClient.create(ClientConfig.newBuilder().setMaxRequestRetry(5).setMaxRequestRetry(2).build());
		BotClient bc = BotClient.create(nc);

		String url = "http://ip.jsontest.com/";

		BotCompletionHandler handler = new BotCompletionHandler() {
			@Override
			public void onCompleted(ReadSession session, final BotMessage bm, final JsonObject response) {
				session.tran(new TransactionJob<Void>() {
					@Override
					public Void handle(WriteSession wsession) throws Exception {
						wsession.pathBy("/botclient_result/1234").property("message", bm.message()).property("botId", bm.botId());
						return null;
					}
				});
			}

			@Override
			public void onThrowable(Throwable t) {
				t.printStackTrace();
			}
		};

		bc.executeGet(url, session, bm, handler).get();

		assertTrue(session.exists("/botclient_result/1234"));

		ReadNode node = session.pathBy("/botclient_result/1234");
		assertEquals("Hello", node.property("message").asString());
		assertEquals("simsimi", node.property("botId").asString());
	}

}
