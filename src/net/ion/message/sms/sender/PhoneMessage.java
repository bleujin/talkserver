package net.ion.message.sms.sender;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Future;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.message.sms.response.MessagingResponse;
import net.ion.message.sms.response.ResponseHandler;
import net.ion.message.sms.sender.SMSConfig.TargetLoc;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.kr.utils.StringUtil;
import org.restlet.data.Method;

import com.google.common.base.Preconditions;

public class PhoneMessage {

	private static ResponseHandler<MessagingResponse> DefaultResponseHandler = new ResponseHandler<MessagingResponse>() {
		@Override
		public MessagingResponse onSuccess(MessagingResponse response) {
			Debug.debug("[MSG_SEND_SUCCESS]", response.getResponse("euc-kr"));
			return response;
		}

		@Override
		public MessagingResponse onFail(MessagingResponse response) {
			Debug.debug("[MSG_SEND_FAILED]", response);
			return response;
		}

		@Override
		public void onThrow(Throwable t) {
			t.printStackTrace();
		}
	};

	private final JsonObject param = new JsonObject();
	private final SMSSender sender;
	private final TargetLoc target;

	private PhoneMessage(SMSSender sender, TargetLoc target) {
		this.sender = sender;
		this.target = target;
	}

	public final static PhoneMessage create(SMSSender sender, String receiverPhone) {
		TargetLoc target = sender.config().target(receiverPhone);
		PhoneMessage result = new PhoneMessage(sender, target);

		result.param.put("member", generateId())
			.put("group_name", receiverPhone)
			.put("deptcode", target.deptCode())
			.put("usercode", target.userCode())
			.put("encoding", "UNICODE")
			.put("to_message", "");
		return result;
	}

	public PhoneMessage from(String fromNum) {

		if (target.isDomestic()) {
			String[] fromNums = StringUtils.split(fromNum, "-");
			Preconditions.checkArgument(fromNums.length == 3, "Phone number format is XXX-XXXX-XXXX");

			param.put("from_num1", fromNums[0]).put("from_num2", fromNums[1]).put("from_num3", fromNums[2]);

		} else {
			param.put("from_num", StringUtils.replace(fromNum, "-", ""));
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
		return sender.send(this, DefaultResponseHandler);
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
