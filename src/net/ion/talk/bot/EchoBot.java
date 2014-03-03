package net.ion.talk.bot;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
import net.ion.talk.bean.Const;

import java.util.Set;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 2:26
 * To change this template use File | Settings | File Templates.
 */
public class EchoBot extends EmbedBot {

    private ScheduledExecutorService es = Executors.newScheduledThreadPool(5);

    public EchoBot(ReadSession rsession) {
        super("echoBot", "http://localhost:9000/bot", rsession);
    }

    @Override
    public void onEnter(String roomId, String sender) throws Exception {

        //if bot
        if (id.equals(sender))
            sendMessage(roomId, "Hello I'm EchoBot. please type: /help", sender);
        else
            sendMessage(roomId, "Hello! " + sender, sender);
    }

    @Override
    public void onExit(String roomId, String sender) throws Exception {

        if (id.equals(sender))
            sendMessage(roomId, "Bye~ see you later!", sender);
        else
            sendMessage(roomId, "Bye! " + sender, sender);
    }

    @Override
    public void onMessage(String roomId, String sender, String message) throws Exception {


        if(StringUtil.startsWith(message, "/help")){
            sendMessage(roomId, "안녕하세요! EchoBot입니다. 봇에게 지연된 응답을 받고 싶으면 \"/delay 초\"라고 대답해주세요. 5초후 예: /delay 5", sender);
        }else if(StringUtil.startsWith(message, "/delay")){
            int delay = Integer.parseInt(StringUtil.stripStart(message, "/delay "));
            sendMessage(roomId, sender + " 사용자에게는 봇이 " +delay +"초 후에 반응합니다.", sender);
            setDelay(roomId, sender, delay);
        }else{
            sendMessage(roomId, message, sender);
        }

    }

    @Override
    public void onFilter(String roomId, String sender, String message, String messageId) throws Exception {
    }


    private void setDelay(final String roomId, final String sender, final int delay) throws Exception {
        setUserProperty(roomId, sender, "delay", delay);
    }

    private void sendMessage(final String roomId, final String message, String sender) throws Exception {

        int delay = getUserProperty(roomId, sender, "delay").intValue(0);

        es.schedule(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                final Set<String> memberList = rsession.pathBy("/rooms/" + roomId + "/members").childrenNames();

                rsession.tranSync(new TransactionJob<Object>() {
                    @Override
                    public Object handle(WriteSession wsession) throws Exception {
                        WriteNode messageNode = wsession.pathBy("/rooms/" + roomId + "/messages/" + new ObjectId().toString())
                                .property(Const.Message.Message, message)
                                .property(Const.Message.Sender, id())
                                .property(Const.Room.RoomId, roomId)
                                .property(Const.Message.Event, Const.Event.onMessage)
                                .property(Const.Message.ClientScript, "client.room().message(args.message)")
                                .property(Const.Message.RequestId, new ObjectId().toString());

                        for (String member : memberList) {
                            if (!member.equals(id()))
                                messageNode.append(Const.Message.Receivers, member);
                        }
                        return null;
                    }
                });
                return null;
            }
        }, delay, TimeUnit.SECONDS);



    }


}
