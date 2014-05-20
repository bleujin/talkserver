package net.ion.talk.script;

import net.ion.framework.util.StringUtil;

public class MessageCommand {

	private BotMessage botMessage;
	private String message;
	private String remains;
	private String[] cmds;

	private MessageCommand(BotMessage botMessage, String message) {
		this.botMessage = botMessage ;
		this.message = message ;
		this.remains = StringUtil.substringAfter(message, " ") ;
		this.cmds = StringUtil.split(message, " ") ;
	}

	public static MessageCommand create(BotMessage botMessage, String message) {
		return new MessageCommand(botMessage, message);
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
