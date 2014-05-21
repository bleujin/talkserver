package net.ion.talk.bot.connect;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.Response;
import net.ion.talk.script.BotMessage;

public class RestClient {

	private NewClient nc;
	private ReadSession session;

	private RestClient(NewClient nc, ReadSession session) {
		this.nc = nc ;
		this.session = session ;
	}

	public static RestClient create(NewClient nc, ReadSession session) {
		return new RestClient(nc, session);
	}
	
	
	public RestRequestBuilder request(String url) {
		return RestRequestBuilder.create(nc, nc.prepareGet(url)) ;
	}
	
	
}