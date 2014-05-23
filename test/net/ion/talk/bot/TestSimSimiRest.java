package net.ion.talk.bot;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

public class TestSimSimiRest extends TestCase {

	
	public void testFirst() throws IOException, InterruptedException, ExecutionException {
		NewClient nc = NewClient.create() ;
		String say = "바보 똥개" ;
		String url = "http://sandbox.api.simsimi.com/request.p";
		
		nc.preparePost(url).addParameter("key", "22441b90-e7a7-4a68-9f5b-6486b0a59676").addParameter("lc", "ko").addParameter("text", say).execute(new AsyncCompletionHandler<Void>() {
			@Override
			public Void onCompleted(Response response) throws Exception {
				Debug.line(response.getStatus().getCode());
				Debug.line(response.getTextBody());
				return null;
			}

			public void onThrowable(Throwable ex) {
				// handler.onThrow(message, ex);
			}
		}).get();
		
		nc.close(); 
	}	
}
