package net.ion.talk.bot.connect;

import net.ion.craken.node.ReadSession;
import net.ion.radon.aclient.NewClient;

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

    public RestRequestBuilder putRequest(String url) {
        return RestRequestBuilder.create(nc, nc.preparePut(url)) ;
    }

}