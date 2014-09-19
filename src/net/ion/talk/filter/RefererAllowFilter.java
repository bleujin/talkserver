package net.ion.talk.filter;

import java.net.SocketAddress;

import net.ion.framework.util.StringUtil;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEvent.EventType;

public class RefererAllowFilter implements HttpHandler {

	private String[] address;

	public RefererAllowFilter() {
		this("localhost, 127.0.0.1");
	}

	public RefererAllowFilter(String addresss) {
		this.address = StringUtil.split(addresss, ", ");
	}

	public static RefererAllowFilter test() {
		return new RefererAllowFilter("localhost, 127.0.0.1");
	}

	@Override
	public void onEvent(EventType event, Radon radon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
		SocketAddress remote = request.remoteAddress() ;
		
	}

}
