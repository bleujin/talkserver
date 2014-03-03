package net.ion.talk.bot;

import net.ion.craken.node.*;
import net.ion.framework.util.RandomUtil;
import net.ion.talk.bean.Const;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 2.
 * Time: 오후 4:20
 * To change this template use File | Settings | File Templates.
 */
public class ChatBot extends EmbedBot {

    public ChatBot(ReadSession rsession) {
        super("chatBot", "http://localhost:9000/bot", rsession);
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

    private String setFilter(Set<String> members, String message, String clientScript) {

        StringBuilder sb = new StringBuilder();
        if(message.equals("야")){

            sb.append("client.fontSize(200); ");
            sb.append(clientScript);

            for(String member : members){
                sb.append(" client.character(\""+member+"\").motion(\""+ (RandomUtil.nextInt(3)+1) +"\");");

            }
        }

        return sb.toString();
    }
}
