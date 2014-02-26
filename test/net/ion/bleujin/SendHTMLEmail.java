package net.ion.bleujin;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendHTMLEmail {

	
	public static void main(String[] args) {

		// Recipient's email ID needs to be mentioned.
		String to = "bleujin@i-on.net";

		// Sender's email ID needs to be mentioned
		String from = "bleujin@i-on.net";

		// Assuming you are sending email from localhost
		String host = "smtp.i-on.net";

		// Get system properties
		Properties props = System.getProperties();

		// Setup mail server
		props.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(props);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject("This is the Subject Line!");

			// Send the actual HTML message, as big as you like
			message.setContent("<h1>This is actual message</h1>", "text/html");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
