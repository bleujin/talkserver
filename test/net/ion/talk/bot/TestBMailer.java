package net.ion.talk.bot;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;
import net.ion.framework.mail.BBotMailer;

import org.apache.lucene.index.CorruptIndexException;

public class TestBMailer extends TestCase {

	public void testFirst() throws CorruptIndexException, IOException {
		BBotMailer mailer = BBotMailer.create();
		
		mailer.send("airkjh@i-on.net", "도움말");
	}
}
