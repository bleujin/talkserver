package net.ion.talk.bot;

import net.ion.framework.util.MapUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 19.
 * Time: 오후 4:56
 * To change this template use File | Settings | File Templates.
 */
public class BotManager {

    private Map<String, EmbedBot> botMap = MapUtil.newMap();

    public static BotManager create() {
        return new BotManager();
    }

    public void registerBot(EmbedBot bot) {
        botMap.put(bot.id(), bot);
    }

    public void unregisterBot(EmbedBot bot) {
        botMap.remove(bot.id());
    }

    public EmbedBot getBot(String id) {
        return botMap.get(id);
    }
}
