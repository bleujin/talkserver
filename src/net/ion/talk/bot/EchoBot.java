package net.ion.talk.bot;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.craken.node.ReadSession;
import net.ion.framework.util.StringUtil;


public class EchoBot extends EmbedBot {

    private ScheduledExecutorService ses;

    public EchoBot(ReadSession rsession, ScheduledExecutorService ses) {
        super("echoBot", "메아리봇" ,"흠..?", "http://localhost:9000/bot", rsession);
        this.ses = ses;
    }

    @Override
    public boolean isSyncBot() {
        return false;
    }

    @Override
    public void onEnter(String roomId, String sender) throws Exception {

        //if bot
        if (id.equals(sender))
            sendMessage(roomId, sender, "Hello I'm EchoBot. please type: /help");
        else
            sendMessage(roomId, sender, "Hello! " + sender);
    }

    @Override
    public void onExit(String roomId, String sender) throws Exception {

        if (id.equals(sender))
            sendMessage(roomId, sender, "Bye~ see you later!");
        else
            sendMessage(roomId, sender, "Bye! " + sender);
    }

    @Override
    public void onMessage(String roomId, String sender, String message) throws Exception {


        if(StringUtil.startsWith(message, "/help")){
            sendMessage(roomId, sender, "안녕하세요! EchoBot입니다. 봇에게 지연된 응답을 받고 싶으면 \"/delay 초\"라고 대답해주세요. 5초후 예: /delay 5");
        }else if(StringUtil.startsWith(message, "/delay")){
            int delay = Integer.parseInt(StringUtil.stripStart(message, "/delay "));
            sendMessage(roomId, sender, sender + " 사용자에게는 봇이 " +delay +"초 후에 반응합니다.");
            setDelay(roomId, sender, delay);
        }else{
            sendMessage(roomId, sender, message);
        }

    }

    @Override
    public void onFilter(String roomId, String sender, String message, String messageId) throws Exception {
    }


    private void setDelay(final String roomId, final String sender, final int delay) throws Exception {
        setUserProperty(roomId, sender, "delay", delay);
    }

    @Override
    protected void sendMessage(final String roomId, final String sender, final String message) throws Exception {

        int delay = getUserProperty(roomId, sender, "delay").intValue(0);

        ses.schedule(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                EchoBot.super.sendMessage(roomId, sender, message);

                return null;
            }
        }, delay, TimeUnit.SECONDS);



    }


}
