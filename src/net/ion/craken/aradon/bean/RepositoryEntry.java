package net.ion.craken.aradon.bean;

import java.io.IOException;

import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;

import net.ion.craken.loaders.lucene.ISearcherWorkspace;
import net.ion.craken.loaders.lucene.ISearcherWorkspaceConfig;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.talk.util.NetworkUtil;

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
	
	public static RepositoryEntry testSoloFile(String filePath) throws IOException {
//		GlobalConfiguration gconfig = GlobalConfigurationBuilder.defaultClusteredBuilder().transport().clusterName("toontalk").nodeName(NetworkUtil.hostAddress()).build();
//		RepositoryImpl repo = RepositoryImpl.create(gconfig);
		GlobalConfiguration gconfig = GlobalConfigurationBuilder.defaultClusteredBuilder().transport().nodeName(NetworkUtil.hostAddress()).build();
		RepositoryImpl repo = RepositoryImpl.create(gconfig) ;

		repo.defineWorkspace("working", ISearcherWorkspaceConfig.create().location(filePath).maxNodeEntry(5000)) ;
		repo.start() ;
		return new RepositoryEntry(repo, "working");
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
