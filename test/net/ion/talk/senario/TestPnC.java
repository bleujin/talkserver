package net.ion.talk.senario;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.RandomUtil;
import junit.framework.TestCase;

public class TestPnC extends TestCase {

	public void testname() throws Exception {
		final ArrayBlockingQueue<String> aq = new ArrayBlockingQueue<String>(100);

		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(RandomUtil.nextInt(10) * 200);
						aq.put(RandomUtil.nextRandomString(10));
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		//////////
		
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(RandomUtil.nextInt(10) * 200);
						String rndMsg = aq.take();
						Debug.line(rndMsg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		new InfinityThread().startNJoin();
	}

	public void testCase2() throws Exception {

		ExecutorService es = Executors.newCachedThreadPool();
		

		Future<Void> future = es.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				boolean infinity = true ;
				while (infinity) {
					Thread.sleep(RandomUtil.nextInt(10) * 200);
					String msg = RandomUtil.nextRandomString(10);
					
					
					Debug.line(msg);
				}
				return null;
			}
		});
		
		////mmmmmm
		
		

		new InfinityThread().startNJoin();

	}
	
	
	public void testSchedule() throws Exception {
		boolean infinity = true ;
		while(infinity){
			Thread.sleep(1000);
			Debug.line(); 
		}
		
		new InfinityThread().startNJoin(); 
	}

	public void testSchedule2() throws Exception {
		final ScheduledExecutorService es = Executors.newScheduledThreadPool(2) ;
		
		es.schedule(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					Debug.line();
					// .......
				} finally {
					es.schedule(this, 2, TimeUnit.SECONDS) ;
				}
				return null;
			}
		}, 1, TimeUnit.SECONDS) ;
		
		
		new InfinityThread().startNJoin(); 
	}
	
	
	public void testTime() throws Exception {
		long time = Calendar.getInstance().getTime().getTime() ;
		Debug.line(new Date(time),TimeZone.getTimeZone("GMT"), TimeZone.getAvailableIDs()) ;
		
		GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("Etc/GMT+0")) ;
		Debug.line(g.getTimeInMillis(), System.currentTimeMillis(), new Date().getTime());
		
		
	}
	
}
