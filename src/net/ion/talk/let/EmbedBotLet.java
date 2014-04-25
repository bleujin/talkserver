package net.ion.talk.let;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.aradon.bean.RhinoEntry;
import net.ion.craken.node.ReadSession;
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

import java.lang.reflect.InvocationTargetException;

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
            protected void call(EmbedBot bot, MessageBean message) throws Exception {
                bot.onEnter(message.roomId, message.sender);
            }
        },
        onExit{
            @Override
            protected void call(EmbedBot bot, MessageBean message) throws Exception {
                bot.onExit(message.roomId, message.sender);
            }
        },
        onMessage{
            @Override
            protected void call(EmbedBot bot, MessageBean message) throws Exception {
                bot.onMessage(message.roomId, message.sender, message.message);
            }
        },
        onFilter{
            @Override
            protected void call(EmbedBot bot, MessageBean message) throws Exception {
                bot.onFilter(message.roomId, message.sender, message.message, message.messageId);
            }
        };

        protected abstract void call(EmbedBot bot, MessageBean message) throws Exception;
    }

    @Post
    public Representation post(@AnContext TreeContext context, @AnRequest InnerRequest request, @FormBean MessageBean messageBean) throws Exception {
        BotManager botManager = context.getAttributeObject(BotManager.class.getCanonicalName(), BotManager.class);

        EmbedBot bot = botManager.getBot(messageBean.botId);
        if(bot==null || messageBean.event==null || messageBean.sender==null || messageBean.roomId==null || messageBean.message==null || messageBean.messageId==null)
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

        try {
            Event.valueOf(messageBean.event).call(bot, messageBean);
        } catch (IllegalArgumentException e) {
            return new StringRepresentation(TalkResponseBuilder.failResponse(e));
        }
        String response = TalkResponseBuilder.makeResponse(new ObjectId().toString(), "");

        return new StringRepresentation(response);
    }


    class MessageBean{
        private String botId;
        private String event;
        private String sender;
        private String roomId;
        private String message;
        private String messageId;
    }

}