package net.ion.talk.bot;

import com.google.common.base.Splitter;
import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import org.apache.commons.lang.WordUtils;

public class LineCalculatorTest extends TestCase {

    public void testSplice() {
        String message = "Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong";
        Iterable<String> split = Splitter.fixedLength(25).split(message);

        for(String chunk : split) {
            Debug.line(chunk);
        }
    }

    public void testHeightCalculation_oneVeryLongWord() {
        String message = "Very Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text";
        int lineNum = LineCalculator.linesOf(message);

        assertEquals(9, lineNum);
    }

    public void testHeightCalculation_oneVeryLongWord_debugPrint() {
        String message = "Very Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text";
        Debug.line(LineCalculator.wrap(message));
    }

    public void testHeightCalculation_manyShortWord() {
        String message = "Very long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long text";
        int lineNum = LineCalculator.linesOf(message);

        assertEquals(7, lineNum);
    }

    public void testHeightCalculation_manyShortWord_debugPrint() {
        String message = "Very long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long text";

        Debug.line(LineCalculator.wrap(message));
    }

    public void testHeightCalculation_manyShortHangul() {
        String message = "아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복되다가 끝";
    }

    public void testWordWrap_InKor() {
        String message = "아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복 아주 그냥 막 그냥 긴 텍스트가 반복되다가 끝";
        String wrap = WordUtils.wrap(message, LineCalculator.MAX_CHARS_PER_LINE);
        Debug.line(wrap);
    }

    public void testWordWrap_InEng() {
        String message = "Very long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long text";
        String wrap = WordUtils.wrap(message, LineCalculator.MAX_CHARS_PER_LINE);
        Debug.line(wrap);
    }


}
