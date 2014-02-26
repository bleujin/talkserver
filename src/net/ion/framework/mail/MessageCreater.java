package net.ion.framework.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public interface MessageCreater {
	public MimeMessage makeMessage(MimeMessage initMessage) throws MessagingException ;
}
