package net.ion.framework.mail;

import java.util.concurrent.ScheduledExecutorService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class BBotMailer {

	private String server = "smtp.i-on.net";
	private static final String DUMMY_PWD = "1234";
	
	private ScheduledExecutorService ses;

	private BBotMailer(ScheduledExecutorService ses) {
		this.ses = ses;
	}

	public static BBotMailer create(ScheduledExecutorService ses) {
		return new BBotMailer(ses);
	}

	public void send(final String sender, final String body) {

		Mailer mailer = MailConfigBuilder.create().sendConfig()
			.server(server)
			.mailUserId(sender)
			.mailUserPwd(DUMMY_PWD)						// I-ON SMTP Server does not require authentication when send mail
			.buildConfig()
			.confirmValidOfSendMailConfig()
			.createMailer()
			.executors(ses);

		mailer.sendMail(new MessageCreater() {
			@Override
			public MimeMessage makeMessage(MimeMessage msg) throws MessagingException {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress("b@i-on.net"));
				msg.setSubject("B@ Bot Request", "text/plain; charset=utf-8");
				msg.setContent(body, "text/plain; charset=utf-8");
				return msg;
			}	
		});
	}
}
