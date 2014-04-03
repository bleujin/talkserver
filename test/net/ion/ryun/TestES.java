package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.framework.util.InfinityThread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 3.
 * Time: 오후 3:38
 * To change this template use File | Settings | File Templates.
 */
public class TestES extends TestCase{

    public void testFirst() throws Exception {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

        ses.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Finish 1");
            }
        }, 5, TimeUnit.SECONDS);

        ses.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Finish 2");
            }
        }, 5, TimeUnit.SECONDS);
        ses.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Finish 3");
            }
        }, 5, TimeUnit.SECONDS);
        ses.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Finish 4");
            }
        }, 5, TimeUnit.SECONDS);
        ses.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Finish 5");
            }
        }, 5, TimeUnit.SECONDS);



        new InfinityThread().startNJoin();


    }
}
