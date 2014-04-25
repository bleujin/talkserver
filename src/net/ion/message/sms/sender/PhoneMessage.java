package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Future;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.message.sms.response.MessagingResponse;
import net.ion.message.sms.response.ResponseHandler;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.kr.utils.StringUtil;
import org.restlet.data.Method;

import com.google.common.base.Preconditions;

public class PhoneMessage {

	private final JsonObject param = new JsonObject();
	private final SMSSender sender;
	private final TargetLoc target;

	private PhoneMessage(SMSSender sender, TargetLoc target) {
		this.sender = sender;
		this.target = target;
	}

	public final static PhoneMessage create(SMSSender sender, String receiverPhone) {
		TargetLoc target = TargetLoc.select(receiverPhone);
		PhoneMessage result = new PhoneMessage(sender, target);

		result.param.put("member", generateId())
			.put("group_name", receiverPhone)
			.put("deptcode", target.deptCode())
			.put("usercode", target.userCode())
			.put("encoding", "UNICODE")
			.put("to_message", "");
		return result;
	}

	public PhoneMessage from(String exchangeNo, String prefixNo, String postfixNo) {

		if (target.isDomestic()) {
			param.put("from_num1", exchangeNo).put("from_num2", prefixNo).put("from_num3", postfixNo);

		} else {
			param.put("from_num", exchangeNo + prefixNo + postfixNo);
		}

		return this;
	}

    private static String generateId() {
        long number = (long) Math.floor(Math.random() * 900000000L) + 100000000L;
        return String.valueOf(number);
    }
	
	private String toQueryString() {
		StringBuilder queryString = new StringBuilder("?");

		Set<String> keys = param.toMap().keySet();

		for (String key : keys) {
			String value = param.asString(key);

			queryString.append(String.format("%s=%s&", key, value));
		}

		return queryString.substring(0, queryString.length() - 1);
	}

	public Request toRequest() {
		if (isNotValid()) {
			throw new IllegalArgumentException("message too short or too large : " + messageContent());
		}

		RequestBuilder builder = new RequestBuilder().setUrl(target.handlerURL()).setMethod(Method.POST);
		Set<String> keys = param.toMap().keySet();

		for (String key : keys) {
			String value = param.asString(key);
			builder.addParameter(key, value);
		}

		return builder.build();
	}

	public Future<MessagingResponse> send() throws IOException {
		return sender.send(this, ResponseHandler.DefaultResponseHandler);
	}

	public <T> Future<T> send(ResponseHandler<T> rhandler) throws IOException {
		return sender.send(this, rhandler);
	}

	
	public PhoneMessage message(String message) {
		param.put("to_message", toUnicode(message));
		return this;
	}

	private String toUnicode(String message) {
		char[] chars = message.toCharArray();
		StringBuilder builder = new StringBuilder();

		for (char c : chars) {
			String unicode = CharUtils.unicodeEscaped(c).replaceAll("\\\\u", "").toUpperCase();
			builder.append(unicode);
		}

		return builder.toString();
	}

	private boolean isNotValid() {
		String messageContent = param.asString("to_message");
		return StringUtil.length(messageContent) < 1 || StringUtil.length(messageContent) > 360;
	}

	private String messageContent() {
		return param.asString("to_message");
	}

}


enum TargetLoc {
	Domestic {
		public String deptCode() {
			return "8J-N2W-G1";
		}
		public String userCode() {
			return "ioncom2";
		}
		public String handlerURL() {
			return "https://toll.surem.com:440/message/direct_call_sms_return_post.asp";
		}

		public String senderPhoneKey() {
			return "group_name";
		}
		
		public boolean isDomestic() {
			return true ;
		}
		
	}, International {
		public String deptCode() {
			return "JM-BWB-P6";
		}
		public String userCode() {
			return "ioncom";
		}
		public String handlerURL() {
			return "https://toll.surem.com:440/message/direct_INTL_return_post.asp";
		}
		public String senderPhoneKey() {
			return "group_name";
		}
		public boolean isDomestic() {
			return false ;
		}
		
	} ;
	
	public abstract String deptCode() ;
	public abstract String userCode() ;
	public abstract String handlerURL() ;
	public abstract String senderPhoneKey() ;
	public abstract boolean isDomestic() ;
	public String callBackURL(){
		return "http://127.0.0.1/callback" ;
	}
	
	public static TargetLoc select(String receiverPhone){
		return receiverPhone.startsWith("+") ? TargetLoc.International : TargetLoc.Domestic ;
	}
}
