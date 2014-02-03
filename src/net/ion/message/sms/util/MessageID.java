package net.ion.message.sms.util;

import java.util.UUID;

public class MessageID {

    public static String generate() {
        long number = (long) Math.floor(Math.random() * 900000000L) + 100000000L;
        return String.valueOf(number);
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

}
