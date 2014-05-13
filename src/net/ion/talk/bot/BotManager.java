package net.ion.talk.bot;

import java.util.Map;

import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.MapUtil;
import net.ion.talk.bean.Const;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 4:56
 * To change this template use File | Settings | File Templates.
 */
public class BotManager {

    private final ReadSession session;
    private Map<String, EmbedBot> botMap = MapUtil.newMap();

    private BotManager(ReadSession session) {
        this.session = session;
    }

    public static BotManager create(ReadSession session) {
        return new BotManager(session);
    }

    public void registerBot(final EmbedBot bot) throws Exception {
        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/"+bot.id())
                        .property(Const.User.UserId, bot.id())
                        .property(Const.Bot.Nickname, bot.nickname())
                        .property(Const.Bot.StateMessage, bot.stateMessage())
                        .property(Const.Bot.RequestURL, bot.requestURL())
                        .property(Const.Bot.isSyncBot, bot.isSyncBot());

                wsession.pathBy("/bots/"+bot.id()).refTo("user", "/users/"+bot.id());
                return null;
            }
        });
        botMap.put(bot.id(), bot);
    }

    public void unregisterBot(final EmbedBot bot) throws Exception {
        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/"+bot.id()).removeSelf();
                wsession.pathBy("/bots/"+bot.id()).removeSelf();
                return null;
            }
        });
        botMap.remove(bot.id());
    }

    public EmbedBot getBot(String id) {
        return botMap.get(id);
    }
}
