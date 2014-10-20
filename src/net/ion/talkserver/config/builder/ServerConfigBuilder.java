package net.ion.talkserver.config.builder;

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.ion.framework.file.NetUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;
import net.ion.talkserver.config.ServerConfig;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class ServerConfigBuilder {

	private String id = "talk";
	private int port = 9000 ;
	private String password = "dkdldhs" ;
	private int workerCount = 10 ;
	
	private ConfigBuilder parent;
	private String hostName ;

	public ServerConfigBuilder(ConfigBuilder parent) {
		this.parent = parent ;
	}
	
	public ServerConfigBuilder node(Node node) throws XPathExpressionException, DOMException, IOException {
		String id = node.getAttributes().getNamedItem("id").getTextContent();
		int port = NumberUtil.toInt(node.getAttributes().getNamedItem("port").getTextContent(), 9000) ;
		Node pnode = node.getAttributes().getNamedItem("password");
		int workerCount = NumberUtil.toInt(node.getAttributes().getNamedItem("worker").getTextContent(), 9000) ;
		
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node host = (Node) xpath.evaluate("host", node, XPathConstants.NODE);

		return id(id).port(port).password(pnode).workerCount(workerCount).hostName(host == null ? "" :  host.getTextContent()) ;
	}
	
	private ServerConfigBuilder hostName(String hostName) throws IOException {
		this.hostName = hostName ;
		return this;
	}

	private ServerConfigBuilder workerCount(int workerCount) {
		this.workerCount = Math.max(workerCount, 5) ;
		return this;
	}

	private ServerConfigBuilder password(Node pnode) {
		if (pnode != null) this.password = StringUtil.defaultIfEmpty(pnode.getTextContent(), "dkdldhs") ;
		return this;
	}

	public ServerConfigBuilder port(int port){
		this.port = port ;
		return this ;
	}
	
	public ServerConfigBuilder id(String id){
		this.id = StringUtil.defaultIfEmpty(id, "talk") ;
		return this ;
	}
	
	public ConfigBuilder parent(){
		return parent ;
	}

	public ServerConfig build() throws IOException {
		return new ServerConfig(id, port, password, workerCount,  StringUtil.defaultIfEmpty(hostName, NetUtil.findMyPublicIp()));
	}
}
