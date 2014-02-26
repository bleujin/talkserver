package net.ion.framework.mail;

public class HtmlMail {

	private String subject;
	private String content;

	HtmlMail(String subject, String content) {
		this.subject = subject ;
		this.content = content ;
	}

	public static HtmlMail create(String subject, String content) {
		return new HtmlMail(subject, content) ;
	}

}
