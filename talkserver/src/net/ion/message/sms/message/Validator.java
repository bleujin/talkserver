package net.ion.message.sms.message;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ion.framework.parse.gson.JsonObject;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Validator {

    private List<String> mandatories = Lists.newArrayList();
    Map<String, Integer> lengths = Maps.newHashMap();

    public static Validator domesticValidator() {
        Validator validator = new Validator();

        return validator.mandatory("to_message").withinLength("to_message", 360);
    }

    public static Validator internationalValidator() {
        return new Validator().mandatory("to_message", "rurl").withinLength("to_message", 640);
    }

    public Validator mandatory(String... fields) {
        mandatories.addAll(Arrays.asList(fields));
        return this;
    }

    public Validator withinLength(String field, int maxLength) {
        lengths.put(field, maxLength);
        return this;
    }

    public boolean isValid(PhoneMessage message) {
        return true;
    }

    public void checkValidity(PhoneMessage message) {
        JsonObject param = message.getParam();

        checkMandatories(param);
        checkValueLength(param);
    }

    private void checkValueLength(JsonObject param) {
        Iterator<String> iterator = lengths.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Integer maxLength = lengths.get(key);

            if (param.asString(key).length() > maxLength.intValue()) {
                throw new IllegalArgumentException(key + " is too large to send");
            }
        }
    }

    private void checkMandatories(JsonObject param) {
        for (String field : mandatories) {
            if (StringUtils.isEmpty(param.asString(field))) {
                throw new IllegalArgumentException(field + " is null or blank");
            }
        }
    }

}
