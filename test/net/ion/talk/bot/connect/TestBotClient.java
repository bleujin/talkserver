package net.ion.talk.bot.connect;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.talk.bean.Const;
import net.ion.talk.script.BotMessage;

public class TestBotClient extends TestCase {

	public void testFirst() throws IOException, InterruptedException, ExecutionException {

		RepositoryImpl repo = RepositoryImpl.inmemoryCreateWithTest();
		final ReadSession session = repo.login("test");

		NewClient nc = NewClient.create(ClientConfig.newBuilder().setMaxRequestRetry(5).setMaxRequestRetry(2).build());
		RestClient bc = RestClient.create(nc, session);

		String url = "http://ip.jsontest.com/";

		BotCompletionHandler<Void> handler = new BotCompletionHandler<Void>() {
			@Override
			public Void onCompleted(final JsonElement response) {
				session.tran(new TransactionJob<Void>() {
					@Override
					public Void handle(WriteSession wsession) throws Exception {
						wsession.pathBy("/botclient_result/1234").property("message","Hello").property("botId", "simsimi");
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


    public void testHuebot() throws IOException {
        RepositoryImpl repo = RepositoryImpl.inmemoryCreateWithTest();
        ReadSession session = repo.login("test");

        NewClient nc = NewClient.create() ;
        RestClient rc = RestClient.create(nc, session);

        String url = "http://192.168.11.16/api/newdeveloper/lights/1/state";
        String messageBody = new JsonObject().put("on", false).toString();

        Debug.line(messageBody);

        rc.putRequest(url).setBody(messageBody).put(new BotCompletionHandler<Object>() {
            @Override
            public Object onCompleted(JsonElement response) {
                JsonObject jsonObject = response.getAsJsonArray().get(0).getAsJsonObject();

                Debug.line(jsonObject.has("success"));
                return null;
            }

            @Override
            public Object onThrowable(Throwable t) {
                return null;
            }
        });

    }
	
	
}
