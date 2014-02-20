package net.ion.bleujin;

import junit.framework.TestCase;

public class TestChecked extends TestCase {

	
	public void testCacheNull() throws Exception {
		boolean cached = false ;
		try {
			throw new NullPointerException() ;
		} catch(Exception ex){
			cached = true ;
		}
		assertTrue(cached);
	}

	public void testCacheIllegalArgument() throws Exception {
		boolean cached = false ;
		try {
			throw new IllegalArgumentException() ;
		} catch(Exception ex){
			cached = true ;
		}
		assertTrue(cached);
	}

}
