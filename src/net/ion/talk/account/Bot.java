package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
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

import java.util.Map;

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
    private boolean isBot;
    private boolean isEnter;

    public Bot(String userId, ReadSession session, NewClient newClient) {
        super(userId, Type.Bot);
        this.newClient = newClient;
        this.session = session;
    }

    @Override
    public Object onMessage(TalkResponse response) {
        try {
            return buildRequest(response).execute(new AsyncCompletionHandler<Object>() {
                @Override
                public Object onCompleted(Response response) throws Exception {
                    return response.getStatusCode();
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }

    }

    private NewClient.BoundRequestBuilder buildRequest(TalkResponse response) {

        String requestURL = session.pathBy("/users/" + accountId()).property("requestURL").stringValue();
        ReadNode notifyNode = session.pathBy("/notifies/" + accountId() + "/" + response.toJsonObject().asString("notifyId"));
        ReadNode messageNode = notifyNode.ref("message");
        String message = messageNode.property("message").stringValue();

        NewClient.BoundRequestBuilder builder = newClient.preparePost(requestURL);
        builder.addParameter("roomId", notifyNode.ref("roomId").fqn().name())
            .addParameter("sender", messageNode.property("roomId").stringValue())
            .addParameter("message", message)
            .addParameter("event", selectEvent(message));
        return builder;
    }

    private String selectEvent(String message){
        if(new URIPattern(Const.Message.ROOM_IN_AND_OUT_PATTERN).match(message)){
            Map<String, String> resolveMap = resolve(Const.Message.ROOM_IN_AND_OUT_PATTERN, message);
            isBot = resolveMap.get("userId").equals(accountId());
            isEnter = resolveMap.get("event").equals(Const.Room.Enter);

            if(isBot)
                return isEnter ? Const.Message.onInvited : Const.Message.onExit;
            else
                return isEnter ? Const.Message.onUserEnter : Const.Message.onUserExit;

        }else
            return Const.Message.onMessage;
    }

    private Map<String, String> resolve(String pattern, String message){
        URIResolveResult resolver = new URIResolver(message).resolve(new URIPattern(pattern));
        Map<String, String> result = MapUtil.newMap() ;

        for(String name : resolver.names()){
            result.put(name, ObjectUtil.toString(resolver.get(name))) ;
        }

        return result ;
    }

}
