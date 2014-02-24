package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.util.uriparser.URIPattern;
import net.ion.radon.util.uriparser.URIResolveResult;
import net.ion.radon.util.uriparser.URIResolver;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 5:04
 * To change this template use File | Settings | File Templates.
 */
public class Bot extends Account {
    private final NewClient newClient;
    private ReadSession session;
    public Bot(String userId, ReadSession session, NewClient newClient) {
        super(userId, Type.Bot);
        this.newClient = newClient;
        this.session = session;
    }

    @Override
    public Object onMessage(TalkResponse response) throws IOException, ExecutionException, InterruptedException {
            return buildRequest(response).execute(new AsyncCompletionHandler<Object>() {
                @Override
                public Object onCompleted(Response response) throws Exception {
                    return response.getStatusCode();
                }
            }).get();
    }

    private NewClient.BoundRequestBuilder buildRequest(TalkResponse response) {

        String requestURL = session.pathBy("/users/" + accountId()).property(Const.Bot.RequestURL).stringValue();
        ReadNode message = getMessageByNotifyId(response.toJsonObject().asString(Const.Message.NotifyId));

        NewClient.BoundRequestBuilder builder = newClient.preparePost(requestURL);
        builder.addParameter(Const.Message.Event, message.property(Const.Message.Event).stringValue())
                .addParameter(Const.Message.Sender, message.property(Const.Message.Sender).stringValue())
                .addParameter(Const.Bot.BotId, accountId())
                .addParameter(Const.Message.Message, message.property(Const.Message.Message).stringValue())
                .addParameter(Const.Room.RoomId, message.property(Const.Room.RoomId).stringValue());
        return builder;
    }

    private ReadNode getMessageByNotifyId(String notifyId) {
        ReadNode notifyNode = session.pathBy("/notifies/" + accountId() + "/" + notifyId);
        ReadNode messageNode = notifyNode.ref(Const.Message.Message);
        return messageNode;
    }

}
