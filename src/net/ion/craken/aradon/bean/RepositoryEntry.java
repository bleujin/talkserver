package net.ion.craken.aradon.bean;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;

public class RepositoryEntry  {

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
	
	public RepositoryImpl repository(){
        return r;
    }


    public void start() {
        r.start();
    }
}
