package net.ion.ryun;

import junit.framework.TestCase;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 7.
 * Time: 오전 10:31
 * To change this template use File | Settings | File Templates.
 */
public class TestDestroy extends TestCase{

    public void testFirst() throws Exception {


        ScheduledExecutorService es = Executors.newScheduledThreadPool(5);
        es.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("HelloWorld");
            }
        }, 1, TimeUnit.SECONDS);



        es.shutdown();
        assertNotNull(es);
        assertTrue(es.isTerminated());


    }
}
