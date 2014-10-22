package net.ion.talkserver.config;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.loaders.lucene.ISearcherWorkspaceConfig;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.talk.bean.Const.User;

import org.infinispan.manager.DefaultCacheManager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;


public class TalkConfig {

	private ServerConfig serverConfig;
	private LogConfig logConfig;
	private RepositoryConfig repoConfig ;
	private ScheduledExecutorService ses ;
	private TransactionJob<Void> initJob = TransactionJob.BLANK ;
	
	public TalkConfig(ServerConfig serverConfig, RepositoryConfig repoConfig, LogConfig logConfig) {
		this.serverConfig = serverConfig ;
		this.repoConfig = repoConfig ;
		this.logConfig = logConfig;

		if (serverConfig.workerCount() == 0) {
			ScheduledThreadPoolExecutor newses = new ScheduledThreadPoolExecutor(30, new ThreadFactoryBuilder().setNameFormat("talk-worker-%d").build()) ;
			newses.setKeepAliveTime(30, TimeUnit.SECONDS);
			newses.allowCoreThreadTimeOut(true);
			this.ses = newses ;
		} else {
			this.ses = Executors.newScheduledThreadPool(serverConfig.workerCount(), new ThreadFactoryBuilder().setNameFormat("talk-worker-%d").build()) ;
		}
		
	}

	
	public ScheduledExecutorService executorService(){
		return ses ;
	}
	
	public ServerConfig serverConfig(){
		return serverConfig ;
	}

	public LogConfig logConfig(){
		return logConfig ;
	}


	public RepositoryConfig repoConfig() {
		return repoConfig ;
	}

	
	public RepositoryEntry createREntry() throws IOException{
		RepositoryImpl r = RepositoryImpl.test(new DefaultCacheManager(), serverConfig().id());
		r.defineWorkspaceForTest(repoConfig.wsName(), ISearcherWorkspaceConfig.create().location(repoConfig.talkHomeDir()));
		r.start();

		r.login(repoConfig.wsName()).tran(initJob) ;
		
		return new RepositoryEntry(r, repoConfig.wsName(), this);
	}


	public RepositoryEntry testREntry() throws IOException {
		RepositoryImpl r = RepositoryImpl.test(new DefaultCacheManager(), serverConfig.id());
		r.defineWorkspaceForTest("test", ISearcherWorkspaceConfig.create().location(""));
		r.start();

		r.login(repoConfig.wsName()).tran(initJob) ;
		return new RepositoryEntry(r, "test", this);
	}

	public TalkConfig addTestUser(){
		this.initJob = new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				if (! wsession.exists("/users/hero@i-on.net")) wsession.pathBy("/users/hero@i-on.net").property(User.UserId, "hero@i-on.net").property(User.Password, "1").property(User.NickName, "hero").property(User.StateMessage, "-_-;").property(User.Phone, "1042216492") ;
				if (! wsession.exists("/users/bleujin@i-on.net")) wsession.pathBy("/users/bleujin@i-on.net").property(User.UserId, "bleujin@i-on.net").property(User.Password, "1").property(User.NickName, "bleujin").property(User.StateMessage, "-_-a").property(User.Phone, "1042216492") ;
				if (! wsession.exists("/users/airkjh@i-on.net")) wsession.pathBy("/users/airkjh@i-on.net").property(User.UserId, "airkjh@i-on.net").property(User.Password, "1").property(User.NickName, "airkjh").property(User.StateMessage, "-_-a").property(User.Phone, "1042216491") ;
				return null;
			}
		} ;
		
		return this ;
	}
	
}
