package net.ion.talk.bot;


import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.util.uriparser.URIPattern;
import net.ion.radon.util.uriparser.URIResolveResult;
import net.ion.radon.util.uriparser.URIResolver;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 4:29
 * To change this template use File | Settings | File Templates.
 */
public class BotManager {

    private String ROOM_IN_AND_OUT_PATTERN = "ROOM|{roomId}|{command}|{userId}";

    private Map<String, Bot> botMap = MapUtil.newMap();



    private BotManager() {

    }

    public static BotManager create(){
        return new BotManager();
    }


    public void registerBot(Bot bot){
        botMap.put(bot.id(), bot);
    }

    public void unRegisterBot(Bot bot){
        botMap.remove(bot.id());
    }

    public Bot findBy(String botId){
        return botMap.get(botId);
    }

    public String onMessage(String botId, String message, String sender) {
        String response = "";

        Bot bot = findBy(botId);

        //if In & Out (user or bot)
        if(new URIPattern(ROOM_IN_AND_OUT_PATTERN).match(message)){
            Map<String, String> resolveMap = resolve(ROOM_IN_AND_OUT_PATTERN, message);
            String roomId = resolveMap.get("roomId");
            String userId = resolveMap.get("userId");
            String command = resolveMap.get("command");


            //bot In & Out
            if(userId.equals(bot.id())){
                if(command.equals("ENTER"))
                    response = bot.onInvited(roomId);
                else
                    response = bot.onExit(roomId);
            //user In & Out
            }else if(!userId.equals(bot.id())){
                if(command.equals("ENTER"))
                    response = bot.onEnterUser(roomId, userId);
                else
                    response = bot.onExitUser(roomId, userId);
            }

        //Message
        }else{
            response = bot.onMessage(message, sender);
        }

        return response;
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
