package net.ion.talk.bot;

import java.io.IOException;

import net.ion.craken.node.ReadSession;
import net.ion.emotion.EmotionalState;
import net.ion.emotion.Empathyscope;
import net.ion.framework.util.IOUtil;

import org.antlr.stringtemplate.StringTemplate;

public class ToonBot {

	private ReadSession rsession;
	private String receiverTpl;
	private String sendTpl;
	
	private ToonBot(ReadSession rsession) throws IOException{
		this.rsession = rsession ;
		this.receiverTpl = IOUtil.toStringWithClose(ToonBot.class.getResourceAsStream("receiver.tpl")) ;
		this.sendTpl = IOUtil.toStringWithClose(ToonBot.class.getResourceAsStream("sender.tpl")) ;
	}
	
	public static ToonBot create(ReadSession rsession) throws IOException {
		return new ToonBot(rsession) ;
	}

	public String sendSVG(String roomId, String msgId, String charId) throws IOException {
		String message = rsession.pathBy("/rooms/" + roomId + "/messages/" + msgId).property("message").asString() ;
		EmotionalState es = Empathyscope.feel(message) ;
		
		StringTemplate st = new StringTemplate(sendTpl);
		st.setAttribute("message", message);
		st.setAttribute("charId", charId);
		st.setAttribute("emotion", es.getStrongestEmotion().etype().toString().toLowerCase());

		return st.toString();
	}

}
