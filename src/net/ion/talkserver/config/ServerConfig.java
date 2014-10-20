package net.ion.talkserver.config;


public class ServerConfig {

	private String id = "talk";
	private int port = 9000 ;
	private String password;
	private int workerCount = 10 ;
	private final String hostName;

	public ServerConfig(String id, int port, String password, int workerCount, String hostName) {
		this.id = id ;
		this.port = port ;
		this.password = password ;
		this.workerCount = workerCount ;
		this.hostName = hostName ;
	}
	
	public int port(){
		return port;
	}
	
	public String id() {
		return id ;
	}

	public String password(){
		return password ;
	}

	public int workerCount() {
		return workerCount;
	}

	public String hostName() {
		return hostName ;
	}

	
}
