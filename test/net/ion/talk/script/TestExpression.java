package net.ion.talk.script;

import java.util.List;
import java.util.Map;

import net.ion.craken.expression.ExpressionParser;
import net.ion.craken.expression.Projection;
import net.ion.craken.expression.SelectProjection;
import net.ion.craken.expression.TerminalParser;
import net.ion.craken.expression.ValueObject;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.convert.rows.AdNodeRows;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.craken.node.crud.TestBaseCrud;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonNull;
import net.ion.framework.parse.gson.JsonPrimitive;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.rosetta.Parser;

public class TestExpression extends TestBaseCrud{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/depts/dev").property("name", "dev").refTo("manager", "/emps/bleujin") ;
				wsession.pathBy("/depts/cen").property("name", "cen").refTo("manager", "/emps/bleujin") ;
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").property("age", 20).append("loc", "seoul", "sungnam") ;
				return null;
			}
		}) ;
	}
	
	public void testChild() throws Exception {
		ReadNode dev = session.pathBy("/depts/dev") ;
		TalkResponse response = TalkResponseBuilder.create().newInner().property(dev, "name, manager.name as mname" ).build() ;
		response.debugString(); 
		assertEquals("dev", response.toJsonObject().asString("name"));
		assertEquals("bleujin", response.toJsonObject().asString("mname"));
	}
	
	
	public void testWithArray() throws Exception {
		ReadNode dev = session.pathBy("/depts/dev") ;
		TalkResponse response = TalkResponseBuilder.create().newInner().property(dev, "manager.[loc] as loc, notfound, case when manager.age > 20 then 'old' else 'young' end as age").build() ;
		
		response.debugString();
		assertTrue(response.toJsonObject().get("notfound") == JsonNull.INSTANCE);
		JsonArray locs = response.toJsonObject().asJsonArray("loc") ;
		assertEquals("seoul", locs.get(0).getAsString()) ;
		assertEquals("sungnam", locs.get(1).getAsString()) ;
	}
	
	
	public void testInList() throws Exception {
		ReadChildren children = session.pathBy("/depts").children() ;
		TalkResponse response = TalkResponseBuilder.create().newInner().inlist("nodes").property(children, "name, manager.[loc] as loc, notfound, case when manager.age > 20 then 'old' else 'young' end as age").build() ;
		
		response.debugString();
		
	}
	

	public void xtestProjection() throws Exception {
		ReadNode dev = session.pathBy("/depts/dev") ;
		
		Parser<MyProjection> parser = ExpressionParser.projections2(MyProjection.class) ;
		MyProjection sp = TerminalParser.parse(parser, "name as Name, substring(case when manager.age > 20 then 'high' else 'lower' end, 2) mname, manager.[loc] as loc");
		Map<String, Object> map = sp.map(dev) ;
		
		Debug.line(map); 
	}
	
}


