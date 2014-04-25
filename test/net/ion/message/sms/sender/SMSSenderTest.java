package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;

import org.apache.lucene.analysis.kr.utils.StringUtil;

public class SMSSenderTest extends TestCase {

	public void testCreateSender() throws IOException, ExecutionException, InterruptedException {
		SMSSender sender = SMSSender.create(NewClient.create());
		sender.toPhoneNo("01042216492").message("안녕").from("02", "3430", "1751").send().get();
	}

	public void testCheckValidity() throws IOException {
		try {
			SMSSender sender = SMSSender.create(NewClient.create());
			PhoneMessage noContentMsg = sender.toPhoneNo("01025704848");
			noContentMsg.send();
			fail();
		} catch (IllegalArgumentException expect) {
		}
	}

	public void testLongMessge() throws IOException {
		SMSSender sender = SMSSender.create(NewClient.create());
		PhoneMessage longContentMessage = sender.toPhoneNo("01025704848").message("가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하");

		try {
			longContentMessage.send();
		} catch (IllegalArgumentException e) {
			Debug.line(e.getMessage());
			assertTrue(e.getMessage().indexOf("too large to send") > -1);
		}
	}

}
