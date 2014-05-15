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
		final ReadSession session = repo.login("test");

		final BotMessage bm = BotMessage.create().botId("simsimi").clientScript(Const.Message.DefaultOnMessageClientScript).message("Hello").sender("simsimi");

		NewClient nc = NewClient.create(ClientConfig.newBuilder().setMaxRequestRetry(5).setMaxRequestRetry(2).build());
		RestClient bc = RestClient.create(nc, session);

		String url = "http://ip.jsontest.com/";

		BotCompletionHandler<Void> handler = new BotCompletionHandler<Void>() {
			@Override
			public Void onCompleted(final JsonObject response) {
				session.tran(new TransactionJob<Void>() {
					@Override
					public Void handle(WriteSession wsession) throws Exception {
						wsession.pathBy("/botclient_result/1234").property("message", bm.message()).property("botId", bm.botId());
						return null;
					}
				});
				return null ;
			}

			@Override
			public Void onThrowable(Throwable t) {
				t.printStackTrace();
				return null ;
			}
		};

		bc.request(url).get(handler);

		assertTrue(session.exists("/botclient_result/1234"));

		ReadNode node = session.pathBy("/botclient_result/1234");
		assertEquals("Hello", node.property("message").asString());
		assertEquals("simsimi", node.property("botId").asString());
	}

	
	
	public void testInterface() throws Exception {
		RepositoryImpl repo = RepositoryImpl.inmemoryCreateWithTest();
		ReadSession session = repo.login("test");
		
		NewClient nc = NewClient.create() ;
		RestClient.create(nc, session) ;
	}
	
	
	
}
