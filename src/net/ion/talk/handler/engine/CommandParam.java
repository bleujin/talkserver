package net.ion.talk.handler.engine;

import net.ion.framework.util.StringUtil;
import net.ion.talk.ParameterMap;
import net.ion.talk.TalkMessage;

public class CommandParam {

	private ParameterMap params;
	private String[] cmds;
	private String remains;

	private CommandParam(ParameterMap params, String userMessage) {
		this.params = params ;
		this.remains = StringUtil.substringAfter(userMessage, " ") ;
		this.cmds = StringUtil.split(userMessage, " ") ;
	}

	public static CommandParam create(TalkMessage tmsg) {
		return new CommandParam(tmsg.params(), tmsg.userMessage());
	}
	
	public String asString(String name){
		return params.asString(name) ;
	}

	public String[] asStrings(String name){
		return params.asStrings(name) ;
	}

	public int asInt(String name){
		return params.asInt(name) ;
	}
	
	public String fnName(){
		return cmds[0].substring(1) ;
	} 

	public String remain(int index){
		return StringUtil.trim(cmds[index + 1]) ;
	} 

	public String remains(){
		return remains ;
	}
	
}
