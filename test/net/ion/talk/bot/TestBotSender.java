package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.util.AradonTester;
import net.ion.talk.util.NetworkUtil;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오전 10:22
 * To change this template use File | Settings | File Templates.
 */
public class TestBotSender extends TestCase implements IServiceLet{

    private BotSender botSender;

    private Aradon aradon;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        botSender = BotSender.create();

        aradon = AradonTester.create().register("bot", "/{botId}/{event}", EnumClass.IMatchMode.STARTWITH, TestBotSender.class).getAradon();
        aradon.startServer(9000);
    }

    public void testOnInvited() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        ListenableFuture<String> response = botSender.sendMessage("echobot", NetworkUtil.getHostAddressWithProtocol("http") + ":9000/bot/echobot", "echobot", "1", "ROOM|1|ENTER|echobot");
        assertEquals("echobot|onInvited|echobot|1|ROOM|1|ENTER|echobot", response.get());
    }

    public void testOnExit() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        ListenableFuture<String> response = botSender.sendMessage("echobot", NetworkUtil.getHostAddressWithProtocol("http") + ":9000/bot/echobot", "echobot", "1", "ROOM|1|EXIT|echobot");
        assertEquals("echobot|onExit|echobot|1|ROOM|1|EXIT|echobot", response.get());
    }

    public void testOnUserEnter() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        ListenableFuture<String> response = botSender.sendMessage("echobot", NetworkUtil.getHostAddressWithProtocol("http") + ":9000/bot/echobot", "ryuneeee", "1", "ROOM|1|ENTER|ryuneeee");
        assertEquals("echobot|onUserEnter|ryuneeee|1|ROOM|1|ENTER|ryuneeee", response.get());
    }

    public void testOnUserExit() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        ListenableFuture<String> response = botSender.sendMessage("echobot", NetworkUtil.getHostAddressWithProtocol("http") + ":9000/bot/echobot", "ryuneeee", "1", "ROOM|1|EXIT|ryuneeee");
        assertEquals("echobot|onUserExit|ryuneeee|1|ROOM|1|EXIT|ryuneeee", response.get());
    }

    @Post
    public Representation post(@AnRequest InnerRequest request){
        String event = request.getAttribute("event");
        String botId = request.getAttribute("botId");
        String sender = request.getParameter("sender");
        String roomId = request.getParameter("roomId");
        String message = request.getParameter("message");
        return new StringRepresentation(botId+"|"+event+"|"+sender+"|"+roomId+"|"+message);
    }

    @Override
    public void tearDown() throws Exception {
        aradon.stop();
        super.tearDown();
    }
}
