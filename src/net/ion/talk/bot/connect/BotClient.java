package net.ion.talk.bot.connect;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.Response;
import net.ion.talk.script.BotMessage;

public class BotClient {

	private NewClient nc;
	
	
	public static BotClient create(NewClient nc) {
		BotClient client = new BotClient();
		client.nc = nc;
		
		return client;
	}
	
	public <T> ListenableFuture<T> executeGet(String url, final ReadSession session, final BotMessage bm, final BotCompletionHandler handler) throws IOException {
		Request request = nc.prepareGet(url).build();
		
		return nc.executeRequest(request, new AsyncCompletionHandler<T>() {
			@Override
			public T onCompleted(Response response) throws Exception {
				JsonObject json = new JsonParser().parse(response.getTextBody()).getAsJsonObject();
				handler.onCompleted(session, bm, json);
				return null;
			}
		});
	}
	
	private BotClient() {}
}