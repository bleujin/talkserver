package net.ion.talk.let;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ObjectId;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.FormBean;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.talk.bot.EmbedBot;
import net.ion.talk.bot.BotManager;
import net.ion.talk.responsebuilder.TalkResponseBuilder;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 4:23
 * To change this template use File | Settings | File Templates.
 */
public class EmbedBotLet implements IServiceLet{

    private enum Event {
        onEnter{
            @Override
            protected String call(EmbedBot bot, MessageBean message) {
                return bot.onEnter(message.roomId, message.userId);
            }
        },
        onExit{
            @Override
            protected String call(EmbedBot bot, MessageBean message) {
                return bot.onExit(message.roomId, message.userId);
            }
        },
        onMessage{
            @Override
            protected String call(EmbedBot bot, MessageBean message) {
                return bot.onMessage(message.roomId, message.sender, message.message);
            }
        };

        protected abstract String call(EmbedBot bot, MessageBean message);
    }

    @Post
    public Representation post(@AnContext TreeContext context, @AnRequest InnerRequest request, @FormBean MessageBean messageBean) throws IOException {
        RepositoryEntry rentry = context.getAttributeObject(RepositoryEntry.EntryName, RepositoryEntry.class);
        RhinoEntry rengine = context.getAttributeObject(RhinoEntry.EntryName, RhinoEntry.class);
        ReadSession rsession = rentry.login();
        BotManager botManager = context.getAttributeObject(BotManager.class.getCanonicalName(), BotManager.class);

        EmbedBot bot = botManager.getBot(messageBean.userId);
        if(bot==null || messageBean.event==null || messageBean.sender==null || messageBean.roomId==null || messageBean.message==null)
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

        String scriptId = new ObjectId().toString();
        Object result;
        String response;
        try {
            String script = Event.valueOf(messageBean.event).call(bot, messageBean);
            if(script==null)
                return new StringRepresentation(TalkResponseBuilder.makeResponse(scriptId, "not executed    "));
            result = rengine.executeScript(rsession, scriptId, script, null);
            response = TalkResponseBuilder.makeResponse(scriptId, result).toString();
        } catch (IllegalArgumentException e) {
            return new JsonObjectRepresentation(TalkResponseBuilder.makeResponse(e));
        }

        return new StringRepresentation(response);
    }


    class MessageBean{
        private String userId;
        private String event;
        private String sender;
        private String roomId;
        private String message;
    }


}