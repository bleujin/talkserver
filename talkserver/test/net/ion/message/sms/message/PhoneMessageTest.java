package net.ion.message.sms.message;

import junit.framework.TestCase;


public class PhoneMessageTest extends TestCase {

    public void testUnicodeMsg() {
        PhoneMessage message = new PhoneMessage("");
        message.message("안녕");

        System.out.println(message.getParam().get("to_message"));
    }


}
