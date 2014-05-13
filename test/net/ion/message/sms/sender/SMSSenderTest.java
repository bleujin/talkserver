package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

public class SMSSenderTest extends TestCase {

	SMSSender sender = SMSSender.create(NewClient.create());

	public void testCreateSender() throws IOException, ExecutionException, InterruptedException {
		sender.toPhoneNo("010", "9139", "9660").message("안녕").from("02", "3430", "1751").send().get();
	}

	public void testCheckValidity() throws IOException {
		try {
			SMSSender sender = SMSSender.create(NewClient.create());
			PhoneMessage noContentMsg = sender.toPhoneNo("010", "2570", "4848");
			noContentMsg.send();
			fail();
		} catch (IllegalArgumentException expect) {
		}
	}

	public void testLongMessge() throws IOException {

		PhoneMessage longContentMessage = sender.toPhoneNo("010", "2570", "4848").message("가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하");

		try {
			longContentMessage.send();
		} catch (IllegalArgumentException e) {
			Debug.line(e.getMessage());
			assertTrue(e.getMessage().indexOf("message too short or too large") > -1);
		}
	}

	public void testInternationSender() throws InterruptedException, ExecutionException, IOException {
		String nationalCode = "82" ;			// 82 = Korea 1 = USA 62 = Indonesia
		PhoneMessage internationalSMS = sender.toPhoneNo(nationalCode, "10", "9139", "9660").message("파이날 테스트! 이게 성공하면 더 이상은 안보냄!");
		Response response = internationalSMS.send().get();

		assertTrue(response.getTextBody().indexOf("<input type=\"hidden\" name=\"Result\" value=\"SUCCESS\">") > -1);
	}
	
	public void testKoreaCode() {
		String nationalCode = "82" ;
		PhoneMessage message = sender.toPhoneNo(nationalCode, "10", "9139", "9660").message("파이날 테스트! 이게 성공하면 더 이상은 안보냄!");

		assertTrue(message.isDomestic());
	}
}
