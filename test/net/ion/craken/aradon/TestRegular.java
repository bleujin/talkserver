package net.ion.craken.aradon;

import java.util.regex.Pattern;

import junit.framework.TestCase;

public class TestRegular extends TestCase {

	
	public void testExceptComma() throws Exception {
		Pattern p = Pattern.compile("^[^\\.]*$") ;
		
		assertEquals(true, p.matcher("/bleujin").find()) ;
		assertEquals(false, p.matcher("/bleujin.node").find()) ;
		assertEquals(true, p.matcher("/bleujin/node").find()) ;
		assertEquals(false, p.matcher("/bleujin/node.prop").find()) ;
	}
}
