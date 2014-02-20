package net.ion.talk.bot;


import net.ion.framework.util.*;
import net.ion.radon.aclient.*;
import net.ion.radon.util.uriparser.URIPattern;
import net.ion.radon.util.uriparser.URIResolveResult;
import net.ion.radon.util.uriparser.URIResolver;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 4:29
 * To change this template use File | Settings | File Templates.
 */
public class BotSender {

    private String ROOM_IN_AND_OUT_PATTERN = "ROOM|{roomId}|{event}|{userId}";
    private static final String ENTER = "ENTER";
    private static final String onInvited = "onInvited";
    private static final String onExit = "onExit";
    private static final String onUserEnter= "onUserEnter";
    private static final String onUserExit= "onUserExit";
    private static final String onMessage= "onMessage";

    private NewClient nc = NewClient.create(new ClientConfigBean()
            .setMaxConnectionPerHost(5).setMaxRequestRetry(2));

    private BotSender() {
    }

    public static BotSender create(){
        return new BotSender();
    }

    public ListenableFuture<String> sendMessage(String botId, String requestURL, String sender, String roomId, String message) throws IOException {

        String event = getEvent(botId, message);

        return nc.preparePost(requestURL + "/" + event)
                .addParameter("roomId", roomId)
                .addParameter("sender", sender)
                .addParameter("message", message)
                .execute(new AsyncCompletionHandler<String>() {
                    @Override
                    public String onCompleted(Response response) throws Exception {
                        return response.getTextBody();
                    }
                });

    }

    private String getEvent(String botId, String message) {

        //if In & Out (user or bot)
        if(new URIPattern(ROOM_IN_AND_OUT_PATTERN).match(message)){
            Map<String, String> resolveMap = resolve(ROOM_IN_AND_OUT_PATTERN, message);

            //if bot
            if(botId.equals(resolveMap.get("userId")))
                return isEnter(resolveMap.get("event")) ? onInvited : onExit;
            else
                return isEnter(resolveMap.get("event")) ? onUserEnter : onUserExit;

        }else
            return onMessage;
    }


    private boolean isEnter(String command){
        return command.equals(ENTER);
    }


    private Map<String, String> resolve(String pattern, String message){
        URIResolveResult resolver = new URIResolver(message).resolve(new URIPattern(pattern));
        Map<String, String> result = MapUtil.newMap() ;

        for(String name : resolver.names()){
            result.put(name, ObjectUtil.toString(resolver.get(name))) ;
        }

        return result ;
    }


    public void stop() {
        nc.close();
    }
}
