package net.ion.message.sms.util;

import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;

public class MessageIDTest extends TestCase {

    public void testGenerate() {
        String generated = MessageID.generate();
        System.out.println(generated);

        assertEquals(9, generated.length());
    }

    public void testDuplicateIDPercent() {

        List<String> generated = Lists.newArrayList();
        int total = 1000000;

        for(int i = 0; i < total; i++) {
            generated.add(MessageID.generate());
        }

        int actual = generated.size();

        System.out.printf("%.2f", (float)(actual/total));
    }
}
