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
import net.ion.craken.node.crud.TestBaseCrud;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.rosetta.Parser;

public class TestExpression extends TestBaseCrud{

	public void testChild() throws Exception {
		session.tranSync(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/depts/dev").property("name", "dev").refTo("manager", "/emps/bleujin") ;
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").property("age", 20).append("loc", "seoul", "sungnam") ;
				return null;
			}
		}) ;
		
		ReadNode dev = session.pathBy("/depts/dev") ;
		TalkResponse response = TalkResponseBuilder.create().newInner().property(dev, "name, manager@name" ).build() ;
		response.debugString(); 
		
		
		
		Parser<MyProjection> parser = ExpressionParser.projections(MyProjection.class) ;
		MyProjection sp = TerminalParser.parse(parser, "name as Name, substring(case when manager.age > 20 then 'high' else 'lower' end, 2) mname, manager.[loc] as loc");
		
		
		List<Map<String, Object>> list = sp.mapList(ListUtil.toList(dev)) ;
		
		Debug.line(list); 
	}
	
}


class MyProjection extends ValueObject {
	private List<Projection> projections;

	public MyProjection (List<Projection> projections) {
		this.projections = projections;
	}
	
	public List<Map<String, Object>> mapList(Iterable<ReadNode> nodes){
		List<Map<String, Object>> result = ListUtil.newList() ;
		for (ReadNode node : nodes) {
			Map<String, Object> map = MapUtil.newMap() ;
			for (Projection p : projections) {
				String label = p.label() ;
				if (label.endsWith("[]")) {
					map.put(p.label(), p.value(node)) ;
				} else {
					map.put(p.label(), p.value(node)) ;
				}
			}
			result.add(map) ;
		}
		return result ;
	} 
	
	
}
