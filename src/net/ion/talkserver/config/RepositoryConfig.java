package net.ion.talkserver.config;


public class RepositoryConfig {

	private String talkHomeDir ;
	private String wsName ;
	private String webHomeDir;
	private String tplHomeDir;
	
	public RepositoryConfig(String adminHomeDir, String webHomeDir, String tplHomeDir, String wsName) {
		this.talkHomeDir = adminHomeDir ;
		this.webHomeDir = webHomeDir ;
		this.tplHomeDir = tplHomeDir ;
		this.wsName = wsName ;
	}

	
	public String webHomeDir(){
		return webHomeDir ;
	}
	
	public String templateHomeDir(){
		return tplHomeDir ;
	}
	
	
	public String talkHomeDir(){
		return talkHomeDir ;
	}
	
	public String wsName(){
		return wsName ;
	}

}
