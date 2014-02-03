package net.ion.message.sms.sender;

import com.google.common.base.Preconditions;
import net.ion.framework.util.StringUtil;
import net.ion.message.sms.message.Validator;
import net.ion.radon.aclient.NewClient;

public class SMSConfig {

    private String deptCode;
    private String userCode;
    private String handlerURL;
    private String fromPhone = "000-000-0000";
    private Validator validator;

    private NewClient client;
    private String callbackURL = "http://127.0.0.1/callback";

    private boolean isDomesticMessage = true;


    private String senderPhoneKey = "";

    public SMSConfig() {
        this(NewClient.create());
    }

    public SMSConfig(NewClient client) {
        this.client = client;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getHandlerURL() {
        return handlerURL;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public NewClient getClient() {
        return client;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public Validator getValidator() {
        return validator;
    }

    public String getSenderPhoneKey() {
        return senderPhoneKey;
    }

    public boolean isDomesticMessage() {
        return isDomesticMessage;
    }

    public SMSConfig newDomestic() {
        this.deptCode = "8J-N2W-G1";
        this.userCode = "ioncom2";
        this.handlerURL = "https://toll.surem.com:440/message/direct_call_sms_return_post.asp";
        this.validator = Validator.domesticValidator();
        this.senderPhoneKey = "group_name";

        return this;
    }

    public SMSConfig newInternational() {
        this.deptCode = "JM-BWB-P6";
        this.userCode = "ioncom";
        this.handlerURL = "https://toll.surem.com:440/message/direct_INTL_return_post.asp";
        this.validator = Validator.internationalValidator();
        this.isDomesticMessage = false;

        return this;
    }


    public SMSSender create() {
        Preconditions.checkArgument(StringUtil.isNotEmpty(deptCode), "deptCode is null or blank");
        Preconditions.checkArgument(StringUtil.isNotEmpty(userCode), "userCode is null or blank");
        Preconditions.checkArgument(StringUtil.isNotEmpty(handlerURL), "handlerURL is null or blank");

        return new SMSSender(this);
    }

}
