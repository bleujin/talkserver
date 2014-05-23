package net.ion.talk.let;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.RandomUtil;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.ContextParam;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.talk.util.CalUtil;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.io.IOException;

public class PhoneAuthLet implements IServiceLet {

	@Get
	public String isAuthorized(@ContextParam("repository") RepositoryEntry rentry, @FormParam("phone") String phoneNum, @FormParam("code") String authCode) throws IOException {
		ReadSession session = rentry.login();
		ReadNode node = session.ghostBy("/auth/sms/" + phoneNum);

		return String.valueOf(!node.isGhost() && authCode.equals(node.property("code").asString()));
	}
	
	@Post
	public StringRepresentation sendAuthNum(@AnContext TreeContext context, @AnRequest InnerRequest request, @FormParam("phone") final String phone) throws Exception {

		SMSSender smsSender = context.getAttributeObject(SMSSender.class.getCanonicalName(), SMSSender.class);
        RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);

        final String code = generateAuthNum();

        ReadSession rsession = rentry.login();
        rsession.tran(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/auth/sms/"+phone).property("code", code);
                return null;
            }
        });

        smsSender.toPhoneNo(phone).from("02", "3430", "1200").message(smsMsg(phone, code)).send();
        JsonObject result = new JsonObject().put("createAt", CalUtil.gmtTime())
                .put("status", "success");

        return new StringRepresentation(result.toString());
	}
	
	private String smsMsg(String phoneNumber, String authNum) {
		if(phoneNumber.startsWith("+82")) {
			return String.format("툰톡의 인증번호는 %s 번입니다.", authNum);
		} else {
			return String.format("Authorization code for Toontalk is %s", authNum);
		}
	}

	String generateAuthNum() {
		return String.valueOf(RandomUtil.nextRandomInt(999999) + 1);
	}
}