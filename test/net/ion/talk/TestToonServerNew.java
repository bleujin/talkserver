package net.ion.talk;

import net.ion.framework.util.InfinityThread;
import net.ion.talk.let.TestBaseLet;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 3.
 * Time: 오후 3:21
 * To change this template use File | Settings | File Templates.
 */
public class TestToonServerNew extends TestBaseLet{

    public void testRunInfinite() throws Exception {
        tserver.cbuilder().build();
        tserver.startRadon();
        new InfinityThread().startNJoin();
    }
}
