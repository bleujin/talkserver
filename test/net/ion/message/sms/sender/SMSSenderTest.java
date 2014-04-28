package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.message.sms.response.MessagingResponse;
import net.ion.radon.aclient.NewClient;

public class SMSSenderTest extends TestCase {

	SMSSender sender = SMSSender.create(NewClient.create());

	public void testCreateSender() throws IOException, ExecutionException, InterruptedException {
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

		PhoneMessage longContentMessage = sender.toPhoneNo("01025704848").message("가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하");

		try {
			longContentMessage.send();
		} catch (IllegalArgumentException e) {
			Debug.line(e.getMessage());
			assertTrue(e.getMessage().indexOf("too large to send") > -1);
		}
	}

	public void testInternationSender() throws InterruptedException, ExecutionException, IOException {
//		String airkjhSister_USA = "+1-9176552272";
		String jinikNum_indonesia = "+62-81295139955";

		PhoneMessage internationalSMS = sender.toPhoneNo(jinikNum_indonesia).message("파이날 테스트! 이게 성공하면 더 이상은 안보냄!");
		MessagingResponse response = internationalSMS.send().get();

		assertTrue(response.getReponse().indexOf("<input type=\"hidden\" name=\"Result\" value=\"SUCCESS\">") > -1);
	}
	
	public void testKoreaPhoneUsingInternational() throws InterruptedException, ExecutionException, IOException {
		String airkjhNum = "+82-1091399660";
		
		PhoneMessage internationalSMS = sender.toPhoneNo(airkjhNum).message("파이날 테스트! 이게 성공하면 더 이상은 안보냄!");
		MessagingResponse response = internationalSMS.send().get();

		assertTrue(response.getReponse().indexOf("<input type=\"hidden\" name=\"Result\" value=\"SUCCESS\">") > -1);
		
	}

	public void testInvalidInternationalNumber() throws IOException {
		String noSplitFormat = "+11091399660";
		
		try {
			sender.toPhoneNo(noSplitFormat).message("이 메세지는 가면 안됨!!").toRequest();
		} catch (IllegalArgumentException e) {
			Debug.line(e.getMessage());
			assertTrue(e.getMessage().indexOf("Invalid international message format") > -1);
		}
	}

}
