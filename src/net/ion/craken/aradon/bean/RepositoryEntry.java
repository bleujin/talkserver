package net.ion.craken.aradon.bean;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.radon.core.IService;
import net.ion.radon.core.context.OnEventObject;
import net.ion.radon.core.context.OnOrderEventObject;

public class RepositoryEntry implements OnOrderEventObject {

	private RepositoryImpl r;
	private String wsName;
	public final static String EntryName = "repository" ;
	
	private RepositoryEntry(RepositoryImpl r, String wsName) {
		this.r = r ;
		this.wsName = wsName ; 
	}

	public static RepositoryEntry test() throws IOException {
		return new RepositoryEntry(RepositoryImpl.inmemoryCreateWithTest(), "test");

	}
	public ReadSession login() throws IOException {
		return r.login(wsName);
	}

	public void shutdown(){
		r.shutdown() ;
	}
	
	@Override
	public void onEvent(AradonEvent event, IService service) {
		if (event == AradonEvent.START){
			r.start() ;
		} else if (event == AradonEvent.STOP){
			r.shutdown() ;
		}
	}

	@Override
	public int order() {
		return 1;
	}

	
	
	
}
