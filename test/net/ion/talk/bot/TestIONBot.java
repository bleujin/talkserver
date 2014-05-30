package net.ion.talk.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.impl.util.CsvReader;
import net.ion.talk.TalkMessage;
import net.ion.talk.UserConnection;
import net.ion.talk.fake.FakeUserConnection;
import net.ion.talk.script.BotScript;
import net.ion.talk.script.WhisperMessage;

public class TestIONBot extends TestCase {

	private RepositoryEntry r;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.r = RepositoryEntry.test() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		r.shutdown(); 
		super.tearDown();
	}
	
	public void testRead() throws Exception {
		CsvReader reader = new CsvReader(new StringReader("bleujin	0\nhero	1")) ;
		reader.setFieldDelimiter('\t');
		reader.setBlockDelimiter('\n');
		String[] line = reader.readLine() ;
		while(true){
			line = reader.readLine() ;
			if (line == null || line.length == 0 || StringUtil.isBlank(line[0])) break ;
			String name = line[0] ;
			String rule = line[1] ;
			Debug.line(name, rule);
		}
	}
	
	public void testBotLoad() throws Exception {
		ReadSession rsession =  r.login() ;
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(2); 
		BotScript bs = BotScript.create(rsession, ses, NewClient.create()) ;
		bs.scriptExtension(".dscript") ;
		bs.readDir(new File("./bot")) ;
		
		FakeUserConnection source = FakeUserConnection.fake("bleujin@i-on.net");
		TalkMessage tm = TalkMessage.fromJsonString("{'id':'773','script':'/room/sendMessageWith','params':{'receivers':'','roomId':'234','message':'@ionemp find Dev','sender':'hero@i-on.net','senderNickname':'hero','clientScript':'client.room().message(args);','requestId':'773'}}");
		WhisperMessage whisperMsg = WhisperMessage.create(source, tm);
		Object rtn = bs.whisper(source, whisperMsg) ;
		Debug.line(source.receivedMessage());
		
	}
}
