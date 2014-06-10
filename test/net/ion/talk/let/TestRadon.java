package net.ion.talk.let;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.config.RadonConfigurationBuilder;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import junit.framework.TestCase;

public class TestRadon extends TestCase {

	
	public void testRequest() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add("/upload/{userId}/{resourceId}",new HttpHandler() {
			
			@Override
			public int order() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public void onEvent(EventType eventtype, Radon radon) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void handleHttpRequest(HttpRequest httprequest, HttpResponse httpresponse, HttpControl httpcontrol) throws Exception {
				Debug.line(httprequest.uri()) ;
				httpresponse.end() ;
			}
		}).start().get() ;
		

		NewClient nc = NewClient.create();
		Response response = nc.prepareGet("http://localhost:9000/upload/bleujin/123456").execute().get();
		assertEquals(200, response.getStatusCode());
		Debug.line(response.getUTF8Body());
		nc.close();
		
		radon.stop().get() ;
	}
	
}
