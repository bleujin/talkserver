package net.ion.talk.bot;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class LineCalculator {
    public static final int PIXEL_PER_LINE = 20;
    public static final int BUBBLE_PADDING = 10;
    public static final int MAX_CHARS_PER_LINE = 17;
    public static final int MIN_LINE_NUM = 2;

    public static int linesOf(String message) {
        String[] tokens = net.ion.framework.util.StringUtil.split(message, " ");
        int lineNum = 1;

        StringBuilder lineBuffer = new StringBuilder();

        for(String token : tokens) {

            if(token.length() > MAX_CHARS_PER_LINE) {
                Iterable<String> split = Splitter.fixedLength(MAX_CHARS_PER_LINE).split(token);
                lineNum += Iterables.size(split);
                lineBuffer = new StringBuilder(Iterables.getLast(split) + " ");
            } else if(lineBuffer.length() + token.length() + 1 > MAX_CHARS_PER_LINE) {
                lineNum++;
                lineBuffer = new StringBuilder(token);
            } else {
                lineBuffer.append(token);
                lineBuffer.append(" ");
            }
        }

        return Math.max(lineNum, MIN_LINE_NUM);
    }

    public static String wrap(String message) {
        String[] tokens = net.ion.framework.util.StringUtil.split(message, " ");


        StringBuilder lineBuffer = new StringBuilder();
        StringBuilder wholeMessage = new StringBuilder();

        for(String token : tokens) {

            if(token.length() > MAX_CHARS_PER_LINE) {
                Iterable<String> split = Splitter.fixedLength(MAX_CHARS_PER_LINE).split(token);
                lineBuffer = new StringBuilder(Iterables.getLast(split) + " ");

                for(String chunk : split) {
                    wholeMessage.append("\n");
                    wholeMessage.append(chunk);
                }
                wholeMessage.append(" ");

            } else if(lineBuffer.length() + token.length() + 1 > MAX_CHARS_PER_LINE) {
                lineBuffer = new StringBuilder(token);

                wholeMessage.append("\n");
                wholeMessage.append(token);
                wholeMessage.append(" ");

            } else {
                lineBuffer.append(token);
                lineBuffer.append(" ");

                wholeMessage.append(token);
                wholeMessage.append(" ");
            }
        }

        return wholeMessage.toString();
    }
}
