package net.ion.talk.let;

import net.ion.framework.parse.gson.JsonObject;
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
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 4:23
 * To change this template use File | Settings | File Templates.
 */
public class EmbedBotLet implements IServiceLet{

    private enum Event {
        onInvited {
            @Override
            protected void call(EmbedBot bot, MessageBean message) {
                bot.onInvited(message.roomId);
            }
        },
        onExit{
            @Override
            protected void call(EmbedBot bot, MessageBean message) {
                bot.onExit(message.roomId);
            }
        },
        onUserEnter{
            @Override
            protected void call(EmbedBot bot, MessageBean message) {
                bot.onUserEnter(message.roomId, message.userId);
            }
        },
        onUserExit{
            @Override
            protected void call(EmbedBot bot, MessageBean message) {
                bot.onUserExit(message.roomId, message.userId);
            }
        },
        onMessage{
            @Override
            protected void call(EmbedBot bot, MessageBean message) {
                bot.onMessage(message.roomId, message.sender, message.message);
            }
        };

        protected abstract void call(EmbedBot bot, MessageBean message);
    }

    @Post
    public Representation post(@AnContext TreeContext context, @AnRequest InnerRequest request, @FormBean MessageBean messageBean){
        BotManager botManager = context.getAttributeObject(BotManager.class.getCanonicalName(), BotManager.class);

        EmbedBot bot = botManager.getBot(messageBean.userId);
        if(bot==null || messageBean.event==null || messageBean.sender==null || messageBean.roomId==null || messageBean.message==null)
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

        try {
            Event.valueOf(messageBean.event).call(bot, messageBean);
        } catch (IllegalArgumentException e) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }


        JsonObject response = TalkResponseBuilder.create().newInner().property("status", "OK").build().toJsonObject();
        return new JsonObjectRepresentation(response);
    }


    class MessageBean{
        private String userId;
        private String event;
        private String sender;
        private String roomId;
        private String message;
    }


}