package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.message.sms.message.PhoneMessage;

import org.apache.lucene.analysis.kr.utils.StringUtil;

public class SMSSenderTest extends TestCase {

	public void testCreateSender() throws IOException, ExecutionException, InterruptedException {
		SMSSender sender = new SMSConfig().newDomestic().create();
		sender.newMessage("01042216492").message("안녕").from("02-3430-1751").send().get();
	}

	public void testCheckValidity() throws IOException {
		try {
			SMSSender sender = new SMSConfig().newDomestic().create();
			PhoneMessage noContentMsg = sender.newMessage("01025704848");
			noContentMsg.send();
			fail();
		} catch (IllegalArgumentException expect) {
		}
	}

	public void testLongMessge() throws IOException {
		SMSSender sender = new SMSConfig().newDomestic().create();
		PhoneMessage longContentMessage = sender.newMessage("01025704848").message("가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하");

		try {
			longContentMessage.send();
		} catch (IllegalArgumentException e) {
			Debug.line(e.getMessage());
			assertTrue(e.getMessage().indexOf("too large to send") > -1);
		}
	}

	public void testInternationalSender_init() {
		SMSConfig config = new SMSConfig().newInternational();

		assertTrue(StringUtil.isNotBlank(config.deptCode()));
		assertTrue(StringUtil.isNotBlank(config.userCode()));
		assertTrue(StringUtil.isNotBlank(config.handlerURL()));
	}

	public void testInternationalSender_message() throws IOException, ExecutionException, InterruptedException {
		SMSSender sender = new SMSConfig().newInternational().create();
		PhoneMessage msg = sender.newMessage("821025704848").from("02-3430-1751").message("안녕하세요");

        msg.send().get();
		assertEquals("0234301751", msg.getParam().asString("from_num"));
	}
}
