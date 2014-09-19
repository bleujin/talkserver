package net.ion.talk.let;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.RandomUtil;
import net.ion.message.sms.sender.SMSSender;
import net.ion.radon.core.ContextParam;
import net.ion.radon.core.TreeContext;
import net.ion.talk.util.CalUtil;

@Path("/SMSAuth")
public class PhoneAuthLet  {

	private ReadSession rsession;

	public PhoneAuthLet(@ContextParam(RepositoryEntry.EntryName) RepositoryEntry entry) throws IOException{
		this.rsession = entry.login() ;
	}
	
	@GET
	public String isAuthorized(@FormParam("phone") String phoneNum, @FormParam("code") String authCode) throws IOException {
		ReadNode node = rsession.ghostBy("/auth/sms/" + phoneNum);
		return String.valueOf(!node.isGhost() && authCode.equals(node.property("code").asString()));
	}
	
	
	@POST
	public JsonObject sendAuthNum(@Context TreeContext context, @FormParam("phone") final String phone) throws Exception {
		SMSSender smsSender = context.getAttributeObject(SMSSender.class.getCanonicalName(), SMSSender.class);

        final String code = generateAuthNum();

        rsession.tran(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/auth/sms/"+phone).property("code", code);
                return null;
            }
        });

        smsSender.toPhoneNo(phone).from("02", "3430", "1200").message(smsMsg(phone, code)).send();
        return new JsonObject().put("createAt", CalUtil.gmtTime()).put("status", "success");
	}
	
	private String smsMsg(String phoneNumber, String authNum) {
		if(phoneNumber.startsWith("+82")) {
			return String.format("툰톡의 인증번호는 %s 번입니다.", authNum);
		} else {
			return String.format("Authorization code for Toontalk is %s", authNum);
		}
	}

	private String generateAuthNum() {
		return String.valueOf(RandomUtil.nextRandomInt(999999) + 1);
	}
}