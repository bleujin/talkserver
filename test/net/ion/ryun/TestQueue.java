package net.ion.ryun;

import junit.framework.TestCase;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 2.
 * Time: 오후 6:30
 * To change this template use File | Settings | File Templates.
 */
public class TestQueue extends TestCase{

    ArrayBlockingQueue queue = new ArrayBlockingQueue<String>(3);

    public void testFirst() throws Exception {

        queue.offer("one");
        queue.offer("two");
        queue.offer("three");
        queue.offer("four");
        queue.put("five");

    }
}
