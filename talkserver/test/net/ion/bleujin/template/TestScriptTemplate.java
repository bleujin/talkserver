package net.ion.bleujin.template;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.Debug;
import net.ion.talk.let.ScriptTemplate;

public class TestScriptTemplate extends TestCase {

	private ReadSession session;
	private RepositoryImpl r;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		r = RepositoryImpl.inmemoryCreateWithTest();
		r.start() ;
		
		this.session = r.login("test");
		session.tranSync(new TransactionJob<Void>() {

			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/emps").property("name", "employee").property("script", "-- script --")
					.addChild("bleujin").property("name", "bluejin").parent() 
					.addChild("hero").property("name", "heor") ;
				return null;
			}
		}) ;
	}
	
	public void testNodeTemplate() throws Exception {

		ReadNode found = session.pathBy("/emps");
		final Engine engine = session.workspace().parseEngine();
		String result = found.transformer(ScriptTemplate.test(engine)) ;

		
		Debug.line(result) ;
		
	}
	
	public void testTemplateWhenNotExist() throws Exception {
		ReadNode notFound = session.ghostBy("/emps/notFound");
		final Engine engine = session.workspace().parseEngine();
		String result = notFound.transformer(ScriptTemplate.test(engine)) ;	
		Debug.line(result) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		r.shutdown() ;
		super.tearDown();
	}
}
