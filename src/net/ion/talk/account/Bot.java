package net.ion.talk.account;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;

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
    public void onMessage(String notifyId, TalkResponse response)  {
        try {
			buildRequest(response).execute(new AsyncCompletionHandler<Integer>() {
			    @Override
			    public Integer onCompleted(Response response) throws Exception {
			        return response.getStatusCode();
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private NewClient.BoundRequestBuilder buildRequest(TalkResponse response) {

        String requestURL = session.pathBy("/users/" + accountId()).property(Const.Bot.RequestURL).stringValue();
        ReadNode messageNode = getMessageByNotifyId(response.toJsonObject().asString(Const.Message.NotifyId));

        NewClient.BoundRequestBuilder builder = newClient.preparePost(requestURL);
        builder.addParameter(Const.Message.Event, messageNode.property(Const.Message.Event).stringValue())
                .addParameter(Const.Message.MessageId, messageNode.fqn().name())
                .addParameter(Const.Message.Sender, messageNode.property(Const.Message.Sender).stringValue())
                .addParameter(Const.User.UserId, messageNode.property(Const.User.UserId).stringValue())
                .addParameter(Const.Bot.BotId, accountId())
                .addParameter(Const.Message.Message, messageNode.property(Const.Message.Message).stringValue())
                .addParameter(Const.Room.RoomId, messageNode.parent().parent().fqn().name());
        return builder;
    }

    private ReadNode getMessageByNotifyId(String notifyId) {
        ReadNode notifyNode = session.pathBy("/notifies/" + accountId() + "/" + notifyId);
        ReadNode messageNode = notifyNode.ref(Const.Message.Message);
        return messageNode;
    }




}
