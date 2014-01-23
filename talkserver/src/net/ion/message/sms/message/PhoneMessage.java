package net.ion.message.sms.message;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Future;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.message.sms.response.MessagingResponse;
import net.ion.message.sms.sender.SMSSender;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.restlet.data.Method;

import com.google.common.base.Preconditions;

public class PhoneMessage {

    private JsonObject param = new JsonObject();
    private SMSSender sender;

    public PhoneMessage(String serialNo) {
        setAttribute("member", serialNo);
        setAttribute("group_name", "");
        setAttribute("to_message", "");
        setAttribute("encoding", "UNICODE");
    }

    public void setSender(SMSSender sender) {
        this.sender = sender;
    }

    public void setAttribute(String key, String value) {
        param.put(key, value);
    }

    public JsonObject getParam() {
        return param;
    }

    public PhoneMessage from(String fromNum) {

        if(sender.isDomesticMessage()) {
            String[] fromNums = StringUtils.split(fromNum, "-");
            Preconditions.checkArgument(fromNums.length == 3, "Phone number format is XXX-XXXX-XXXX");

            setAttribute("from_num1", fromNums[0]);
            setAttribute("from_num2", fromNums[1]);
            setAttribute("from_num3", fromNums[2]);

        } else {
            setAttribute("from_num", StringUtils.replace(fromNum, "-", ""));
        }

        return this;
    }

    public String toQueryString() {
        StringBuilder queryString = new StringBuilder("?");

        Set<String> keys = param.toMap().keySet();

        for (String key : keys) {
            String value = param.asString(key);

            queryString.append(String.format("%s=%s&", key, value));
        }

        return queryString.substring(0, queryString.length() - 1);
    }

    public Request toRequest(String targetURL, Method method) {

        RequestBuilder builder = new RequestBuilder().setUrl(targetURL).setMethod(method);

        Set<String> keys = param.toMap().keySet();

        for (String key : keys) {
            String value = param.asString(key);
            builder.addParameter(key, value);
        }

        return builder.build();
    }

    public PhoneMessage message(String message) {
        setAttribute("to_message", toUnicode(message));
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

    public Future<MessagingResponse> send() throws IOException {
        return sender.send(this);
    }
}
