package net.ion.talk.senario;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.RandomUtil;

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
		// ////////

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
				boolean infinity = true;
				while (infinity) {
					Thread.sleep(RandomUtil.nextInt(10) * 200);
					String msg = RandomUtil.nextRandomString(10);

					Debug.line(msg);
				}
				return null;
			}
		});

		// //mmmmmm

		new InfinityThread().startNJoin();

	}

	public void testSchedule() throws Exception {
		boolean infinity = true;
		while (infinity) {
			Thread.sleep(1000);
			Debug.line();
		}

		new InfinityThread().startNJoin();
	}

	public void testSchedule2() throws Exception {
		final ScheduledExecutorService es = Executors.newScheduledThreadPool(2);

		es.schedule(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					Debug.line();
					// .......
				} finally {
					es.schedule(this, 2, TimeUnit.SECONDS);
				}
				return null;
			}
		}, 1, TimeUnit.SECONDS);

		new InfinityThread().startNJoin();
	}

	public void testTime() throws Exception {
		long time = Calendar.getInstance().getTime().getTime();
		Debug.line(new Date(time), TimeZone.getTimeZone("GMT"), TimeZone.getAvailableIDs());

		GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("Etc/GMT+0"));
		Debug.line(g.getTimeInMillis(), System.currentTimeMillis(), new Date().getTime());

	}

	public void testCall() throws Exception {
		final Inc inc = new Inc() ;

		for (int i = 0; i < 100000000; i++) {
			inc.inc() ;
		}
		Debug.line(inc.count());
	}
	
	public void testCancel() throws Exception {

		ExecutorService es = Executors.newFixedThreadPool(10);
		
		final Map<String, String> map = new HashMap() ;
		
		for (int i = 0; i < 100000; i++) {
			map.put("" + i, "" + i) ;
		}


		for (int i = 0; i < 100000; i++) {
			es.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					for(Entry<String, String> entry : map.entrySet()){
						Debug.line(entry);
					} 
					return null;
				}
			}) ;

			final int current = RandomUtil.nextInt(1000) ;
			es.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					map.remove("" + current) ;
					return null;
				}
				
			}) ;
		}


		new InfinityThread().startNJoin(); 
	}
}

class Inc {
	private AtomicLong count = new AtomicLong() ;
	
	public void inc(){
		count.incrementAndGet() ;
	}

	public long count(){
		return count.get() ;
	}
	
//	public void dec(){
//		count-- ;
//	}
}
