package net.ion.talk.let;

import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.EnumClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

/**
 * Author: Ryunhee Han
 * Date: 2013. 12. 27.
 */
public class TestStaticFileLet extends TestBaseLet{

    @Override
    public void setUp() throws Exception {
        super.setUp();

        tserver.cbuilder().aradon()
                .sections()
                .restSection("static")
                .path("static").addUrlPattern("/{path}").matchMode(EnumClass.IMatchMode.STARTWITH)
                .handler(StaticFileLet.class).build();

        tserver.startRadon();
    }


    public void testFileExist() throws Exception {
        NewClient nc = tserver.mockClient().real();
        Response response = nc.prepareGet("http://" + InetAddress.getLocalHost().getHostAddress() + ":9000/static/jquery-1.10.2.min.js").execute().get();
        FileInputStream fis = new FileInputStream("./resource/jquery-1.10.2.min.js");
        assertEquals(fis.getChannel().size(), response.getBodyAsBytes().length);
    }

    public void testFileNotExist() throws IOException, ExecutionException, InterruptedException {
        NewClient nc = tserver.mockClient().real();
        Response response = nc.prepareGet("http://" + InetAddress.getLocalHost().getHostAddress() + ":9000/static/notFound").execute().get();
        assertEquals(500, response.getStatusCode());
    }

}
