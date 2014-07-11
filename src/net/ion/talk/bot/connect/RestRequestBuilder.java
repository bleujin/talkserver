package net.ion.talk.bot.connect;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.framework.parse.gson.JsonElement;
import org.restlet.data.Method;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.Cookie;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.NewClient.BoundRequestBuilder;

public class RestRequestBuilder {

	private BoundRequestBuilder reqBuilder;
	private NewClient nc;
	private RestRequestBuilder(NewClient nc, BoundRequestBuilder reqBuilder) {
		this.nc = nc ;
		this.reqBuilder = reqBuilder ;
	}

	public static RestRequestBuilder create(NewClient nc, BoundRequestBuilder reqBuilder) {
		return new RestRequestBuilder(nc, reqBuilder);
	}
	
	public RestRequestBuilder addParameter(String name, String value){
		reqBuilder.addParameter(name, value) ;
		return this ;
	}

	public RestRequestBuilder addHeader(String name, String value){
		reqBuilder.addHeader(name, value) ;
		return this ;
	}

	public RestRequestBuilder addCookie(String domain, String name, String value, String path, int maxAge){
		reqBuilder.addCookie(new Cookie(domain, name, value, path, maxAge, false)) ;
		return this ;
	}

    public RestRequestBuilder setBody(String body) {
        reqBuilder.setBody(body);
        return this;
    }


	public <T> T get(final BotCompletionHandler<T> handler) throws IOException {
		return execute(reqBuilder.setMethod(Method.GET).build(), handler);		
	}

	public <T> T post(final BotCompletionHandler<T> handler) throws IOException{
		return execute(reqBuilder.setMethod(Method.POST).build(), handler) ;	
	}

	public <T> T delete(final BotCompletionHandler<T> handler) throws IOException {
		return execute(reqBuilder.setMethod(Method.DELETE).build(), handler) ;		
	}

	public <T> T put(final BotCompletionHandler<T> handler) throws IOException {
		return execute(reqBuilder.setMethod(Method.PUT).build(), handler);		
	}

	
	private <T> T execute(Request request, final BotCompletionHandler<T> handler) throws IOException {
		try {
			return nc.executeRequest(request, new AsyncCompletionHandler<T>() {
				@Override
				public T onCompleted(Response response) throws Exception {
                    JsonElement element = new JsonParser().parse(response.getTextBody());
					return handler.onCompleted(element);
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace(); 
			throw new IOException(e) ;
		} catch (ExecutionException e) {
			e.printStackTrace(); 
			throw new IOException(e) ;
		}
	}
	
	
	
	
}
