package net.ion.talk.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import net.ion.framework.util.Debug;
import junit.framework.TestCase;

public class TestKeyMaker extends TestCase {

	public void testPublish() throws Exception {
		KeyMaker km = new KeyMaker();
		long expected = km.publishKey("bleujin");
		assertEquals(true, km.isAuthorized("bleujin", expected));
	}

	public void testNotExpried() throws Exception {
		KeyMaker km = new KeyMaker("sample", 1);
		for (int i = 0; i < 5000; i++) {
			long actual = km.publishKey("bleujin");
			assertTrue(km.isAuthorized("bleujin", actual));
			Thread.sleep(1);
		}
	}

	public void testExpiredKey() throws Exception {
		KeyMaker km = new KeyMaker("sample", 1);
		long expected = km.publishKey("bleujin");
		Thread.sleep(2000);
		assertFalse(expected == km.publishKey("bleujin"));
	}
	
	public void testGregorian() throws Exception {
		Calendar ins = GregorianCalendar.getInstance() ;
		Debug.line(ins.getTime().getTime());
		Thread.sleep(789);
		Debug.line(ins.getTime().getTime());
	}
}
