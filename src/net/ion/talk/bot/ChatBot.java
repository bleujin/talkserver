package net.ion.talk.bot;

import static java.lang.Math.sqrt;

import java.io.IOException;
import java.util.Set;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.emotion.Emotion.EType;
import net.ion.emotion.EmotionalState;
import net.ion.emotion.Empathyscope;
import net.ion.framework.util.RandomUtil;
import net.ion.talk.bean.Const;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 2.
 * Time: 오후 4:20
 * To change this template use File | Settings | File Templates.
 */
public class ChatBot extends EmbedBot {


//    public static final int NEUTRAL = -1;
//    public static final int HAPPINESS = 0;
//    public static final int SADNESS = 1;
//    public static final int FEAR = 2;
//    public static final int ANGER = 3;
//    public static final int DISGUST = 4;
//    public static final int SURPRISE = 5;


    public ChatBot(ReadSession rsession) {
        super("chatBot", "채팅효과봇", "채팅을 재미있게!", "http://localhost:9000/bot", rsession);
    }

    @Override
    public boolean isSyncBot() {
        return true;
    }

    @Override
    public void onEnter(String roomId, String userId) throws Exception {

    }

    @Override
    public void onExit(String roomId, String userId) throws Exception {
    }

    @Override
    public void onMessage(String roomId, String sender, String message) throws Exception {
    }

    @Override
    public void onFilter(final String roomId, String sender, final String message, final String messageId) throws Exception {
        final ReadNode messageNode = rsession.pathBy("/rooms/" + roomId + "/messages/" + messageId);
        final String clientScript = messageNode.property(Const.Message.ClientScript).stringValue();

        final Set<String> members = rsession.pathBy("/rooms/" + roomId + "/members/").childrenNames();

        rsession.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/rooms/" + roomId + "/messages/" + messageId)
                        .property(Const.Message.ClientScript, setFilter(members, message, clientScript))
                        .property(Const.Message.Filter, Const.Message.FilterEnabled);
                return null;
            }
        });
    }

    private String setFilter(Set<String> members, String message, String clientScript) throws IOException {
        StringBuilder sb = new StringBuilder();

        EmotionalState emotion = Empathyscope.feel(message);
        EType etype = emotion.getStrongestEmotion().etype();
        int emotionWeight = Double.valueOf(sqrt(sqrt(emotion.getStrongestEmotion().weight()*100))*100).intValue();

        for(String member : members){
            sb.append("client.character(\""+member+"\").motion(\""+ etype +"\").fontSize("+ emotionWeight +").messageBalloon("+ RandomUtil.nextInt(3) +");");
        }

        sb.append(clientScript);

        return sb.toString();
    }
}
