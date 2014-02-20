package net.ion.talk.let;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.talk.bot.Bot;
import net.ion.talk.bot.BotManager;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 4:23
 * To change this template use File | Settings | File Templates.
 */
public class EmbedBotLet implements IServiceLet{

    private static final String onInvited = "onInvited";
    private static final String onExit = "onExit";
    private static final String onUserEnter= "onUserEnter";
    private static final String onUserExit= "onUserExit";
    private static final String onMessage= "onMessage";

    @Post
    public Representation post(@AnContext TreeContext context, @AnRequest InnerRequest request){
        BotManager botManager = context.getAttributeObject(BotManager.class.getCanonicalName(), BotManager.class);

        String botId = request.getAttribute("botId");
        String event = request.getAttribute("event");
        String sender = request.getParameter("sender");
        String roomId = request.getParameter("roomId");
        String message = request.getParameter("message");

        Bot bot = botManager.getBot(botId);
        if(bot==null || event==null || sender==null || roomId==null || message==null)
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

        if(event.equals(onInvited))
            bot.onInvited(roomId);
        else if(event.equals(onExit))
            bot.onExit(roomId);
        else if(event.equals(onUserEnter))
            bot.onUserEnter(roomId, sender);
        else if(event.equals(onUserExit))
            bot.onUserExit(roomId, sender);
        else if(event.equals(onMessage))
            bot.onMessage(roomId, sender, message);
        else
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

        JsonObject response = TalkResponseBuilder.create().newInner().property("status", "OK").build().toJsonObject();

        return new JsonObjectRepresentation(response);
    }
}