package net.ion.talk.let;

import java.io.IOException;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.RandomUtil;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 21.
 * Time: 오후 1:28
 * To change this template use File | Settings | File Templates.
 */
public class SMSAuthLet implements IServiceLet {

    @Post
    public Representation auth(@AnContext TreeContext context, @AnRequest InnerRequest request, @FormParam("phone") final String phone) throws IOException {
        SMSSender smsSender = context.getAttributeObject(SMSSender.class.getCanonicalName(), SMSSender.class);
        RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);

        final int code = RandomUtil.nextInt(999999)+1;

        ReadSession rsession = rentry.login();
        rsession.tran(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/auth/sms/"+phone).property("code", code);
                return null;
            }
        });

        smsSender.toPhoneNo(phone).from("02", "3430", "1200").message("툰톡 인증번호는 [" + code + "] 입니다.").send();

        return TalkResponseBuilder.makeResponse("/user/smsauth", "인증번호를 발송하였습니다.").transformer(TalkResponse.ToStringRepresentation);
    }

}
