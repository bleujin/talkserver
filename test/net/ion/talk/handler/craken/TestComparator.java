package net.ion.talk.handler.craken;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 2.
 * Time: 오후 2:21
 * To change this template use File | Settings | File Templates.
 */
public class TestComparator extends TestCase {

    public void testFirst() throws Exception {

        List<String> list = ListUtil.toList("a", "c", "e", "d", "z", "h", "r");

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String s2) {

                return s2.compareTo(s);
            }
        });

        Debug.line(list);

    }
}
