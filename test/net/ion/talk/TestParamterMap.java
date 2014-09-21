package net.ion.talk;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import junit.framework.TestCase;

public class TestParamterMap extends TestCase{

	
	public void testAddValue() throws Exception {
		ParameterMap pm = ParameterMap.create() ;
		
		pm.addValue("name", "bleujin") ;
		pm.addValue("name", "hero") ;
		
		assertEquals("bleujin", pm.asString("name")) ;
		assertEquals(2, pm.asStrings("name").length) ;
		Debug.line(pm.asStrings("name"));
	}
	
	public void testFromJson() throws Exception {
		JsonObject json = JsonObject.fromString("{'name':'bleujin', 'log':['seoul', 'sungnam'], age:20}") ;
		
		ParameterMap pm = ParameterMap.create(json) ;
		
		Debug.line(pm.asString("name"), pm.asString("log"), pm.asStrings("log"), pm.asString("age")) ;
	}
	
}
