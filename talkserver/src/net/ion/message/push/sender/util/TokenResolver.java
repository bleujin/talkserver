package net.ion.message.push.sender.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenResolver {

    public static boolean isAPNSMessage(String token) {
        Pattern p = Pattern.compile("^[0-9a-f]{64}$");
        Matcher matcher = p.matcher(token);

        return matcher.matches();
    }

}
