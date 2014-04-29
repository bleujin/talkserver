package net.ion.talk.bot;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.ion.craken.node.ReadSession;
import net.ion.framework.mail.MailConfigBuilder;
import net.ion.framework.mail.Mailer;
import net.ion.framework.mail.MessageCreater;
import net.ion.framework.util.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:26
 * To change this template use File | Settings | File Templates.
 */
public class BBot extends EmbedBot {

    private static String ION_SMTP_SERVER = "smtp.i-on.net";
    private ScheduledExecutorService ses;

    public BBot(ReadSession rsession, ScheduledExecutorService ses) {
        super("bBot", "B@Bot", "나는야 B@Bot!", "http://localhost:9000/bot", rsession);
        this.ses = ses;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String requestURL() {
        return requestURL;
    }

    @Override
    public boolean isSyncBot() {
        return false;
    }

    @Override
    public void onEnter(String roomId, String sender) throws Exception {

        //if not bot
        if (id().equals(sender))
            sendMessage(roomId, sender, "안녕하세요. B@bot 입니다! 도움이 필요하다면 \"B@메시지\"이라고 입력해주세요 :) ex) B@도움말, B@식당, B@월차");
    }

    @Override
    public void onExit(String roomId, String sender) throws Exception {
        if (id().equals(sender))
            sendMessage(roomId, sender, "나중에 또 봐요 ~");
    }

    @Override
    public void onMessage(String roomId, String sender, String message) throws Exception {

        if(StringUtil.startsWith(message, "B@")){
            String content = StringUtil.stripStart(message, "B@");
            sendMail(roomId, sender, content);

        }else if(StringUtil.startsWith(message, "/register")){
            String[] split = message.split(" ");

            if(split.length != 3)
                sendMessage(roomId, sender, "잘못 입력하셨습니다. 다시 입력해주세요.");

            setUserProperty(roomId, sender, "account", split[1]);
            setUserProperty(roomId, sender, "password", split[2]);
            sendMessage(roomId, sender, split[1] + " 계정이 정상적으로 등록되었습니다!");

        }
    }

    @Override
    public void onFilter(String roomId, String sender, String message, String messageId) throws Exception {
    }

    protected void sendMessage(final String roomId, final String sender, final String message) throws Exception {

        ses.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                BBot.super.sendMessage(roomId, sender, message);
                return null;
            }
        }).get();

    }

    private void sendMail(final String roomId, final String sender, final String content) throws Exception {

        final String account = getUserProperty(roomId, sender, "account").stringValue();
        final String password = getUserProperty(roomId, sender, "password").stringValue();
        if(StringUtil.isEmpty(account)){
            sendMessage(roomId, sender, "계정 정보가 없습니다. \"/register 이메일 비밀번호\"를 이용하여 계정정보를 입력해주세요.");
            return;
        }

        ses.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                Mailer sendMailer = MailConfigBuilder.create().sendConfig()
                        .server(ION_SMTP_SERVER).mailUserId(account).mailUserPwd(password)
                        .buildConfig().confirmValidOfSendMailConfig().createMailer();


                sendMailer.sendMail(new MessageCreater() {
                    @Override
                    public MimeMessage makeMessage(MimeMessage msg) throws MessagingException {
                        msg.addRecipient(Message.RecipientType.TO, new InternetAddress("b@i-on.net"));
                        msg.setSubject("test", "text/plain; charset=utf-8");
                        msg.setContent(content, "text/plain; charset=utf-8");
                        return msg;
                    }
                });
                return null;
            }
        });

        sendMessage(roomId, sender, "명령을 보냈습니다!");

    }

}
