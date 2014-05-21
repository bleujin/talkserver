package net.ion.talk.script;

import net.ion.framework.util.StringUtil;

public class MessageCommand {

	private String message;
	private String remains;
	private String[] cmds;

	private MessageCommand(String message) {
		this.message = message ;
		this.remains = StringUtil.substringAfter(message, " ") ;
		this.cmds = StringUtil.split(message, " ") ;
	}

	public static MessageCommand create(String message) {
		return new MessageCommand(message);
	}

	public String fnName(){
		return cmds[0];
	} 

	public String remain(int index){
		return StringUtil.trim(cmds[index + 1]) ;
	} 

	public String remains(){
		return remains ;
	}
	
}
