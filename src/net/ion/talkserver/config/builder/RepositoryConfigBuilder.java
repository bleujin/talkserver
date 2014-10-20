package net.ion.talkserver.config.builder;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.ion.framework.util.StringUtil;
import net.ion.talkserver.config.RepositoryConfig;

import org.w3c.dom.Node;

public class RepositoryConfigBuilder {

	private String wsName = "talk" ;
	private String talkHomeDir = "./resource/talk/" ;
	private String webHomeDir = "./resource/toonweb/" ;
	private String tplHomeDir = "./resource/template/" ;
	
	private ConfigBuilder parent;

	public RepositoryConfigBuilder(ConfigBuilder parent){
		this.parent = parent ;
	}
	
	public RepositoryConfigBuilder node(Node rconfig) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		
		Node talkNode = (Node) xpath.evaluate("talk-home", rconfig, XPathConstants.NODE);
		String wname = rconfig.getAttributes().getNamedItem("wsname").getTextContent() ;
		
		return talkHomeDir(talkNode.getTextContent()).wsName(wname);
	}

	public RepositoryConfigBuilder talkHomeDir(String talkHomeDir){
		this.talkHomeDir = StringUtil.defaultIfEmpty(talkHomeDir, "./resource/talk/") ;
		return this ;
	}

	public RepositoryConfigBuilder webHomeDir(String talkHomeDir){
		this.webHomeDir = StringUtil.defaultIfEmpty(talkHomeDir, "./resource/toonweb/") ;
		return this ;
	}
	

	public RepositoryConfigBuilder tplHomeDir(String talkHomeDir){
		this.tplHomeDir = StringUtil.defaultIfEmpty(talkHomeDir, "./resource/template/") ;
		return this ;
	}
	
	public RepositoryConfigBuilder wsName(String wsName){
		this.wsName = StringUtil.defaultIfEmpty(wsName, "talk") ;
		return this ;
	}
	
	
	public ConfigBuilder parent(){
		return parent ;
	}


	public RepositoryConfig build() {
		return new RepositoryConfig(talkHomeDir, webHomeDir, tplHomeDir,  wsName);
	}
	
	
}
