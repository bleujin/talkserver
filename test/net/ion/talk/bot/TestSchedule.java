package net.ion.talk.bot;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.ObjectId;
import junit.framework.TestCase;

public class TestSchedule extends TestCase {

	
	public void testRun() throws Exception {
		final ScheduledExecutorService es = Executors.newScheduledThreadPool(2) ;
		es.schedule(new Callable<Void>(){
			
			ObjectId oid = new ObjectId() ;
			
			@Override
			public Void call() throws Exception {
				
				// TODO Auto-generated method stub
				return null;
			}
		}, 1, TimeUnit.SECONDS) ;
	}
}
