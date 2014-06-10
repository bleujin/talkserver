package net.ion.talk.bot.baseball;

import net.ion.framework.parse.gson.JsonArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

public class PlayerRanker {

    final private static String HITTER_RANK_URL = "http://www.kbreport.com/leader/list/ajax?year_from=2014&year_to=2014&page=1";
    final private static String PITCHER_RANK_URL = "http://www.kbreport.com/leader/pitcher/list/ajax?year_from=2014&year_to=2014&page=1";

    final private static String[] HITTER_ITEMS = {
            "순위", "선수명", "팀명", "경기", "타석", "타수", "안타", "홈런", "득점", "타점", "볼넷", "삼진", "도루", "BABIP", "타율", "출루율", "장타율", "OPS", "wOBA", "WAR"
    };

    final private static String[] PITCHER_ITEMS = {
            "순위", "선수명", "팀명", "승", "패", "세이브", "홀드", "블론", "경기", "선발", "이닝", "삼진", "볼넷", "홈런", "BABIP", "LOB", "ERA", "RA9WAR", "FIP", "kFIP", "WAR"
    };

    enum PlayerParser {
        hitter {
            @Override
            Player parse(Element tr) {
                return parsePlayer(tr, HITTER_ITEMS);
            }
        },
        pitcher {
            @Override
            Player parse(Element tr) {
                return parsePlayer(tr, PITCHER_ITEMS);
            }
        };

        Player parsePlayer(Element tr, String[] parseItems) {
            Iterator<Element> iterator = tr.getElementsByTag("td").iterator();
            int itemIndex = 0;
            Player player = new Player();

            while(iterator.hasNext()) {
                Element td = iterator.next();
                player.setAttr(parseItems[itemIndex++], td.text());
            }

            return player;
        };

        abstract Player parse(Element el) ;
    };

    public static PlayerRanker create() {
        return new PlayerRanker();
    }

    public JsonArray getTopNHitter(int num) throws IOException {
        return getTopNHitter(num, "AVG", "asc");
    }


    public JsonArray getTopNHitter(int num, String sortOption, String sortDirection) throws IOException {
        String url = String.format("%s&orderType=%s&rows=%s&order=%s", HITTER_RANK_URL, sortDirection, num, sortOption);

        return fetchPlayers(url, PlayerParser.hitter);
    }

    public JsonArray getTopNPitcher(int num) throws IOException {
        return getTopNPitcher(num, "ERA", "asc");
    }

    public JsonArray getTopNPitcher(int num, String sortOption, String sortDirection) throws IOException {
        String url = String.format("%s&orderType=%s&rows=%s&order=%s", PITCHER_RANK_URL, sortDirection, num, sortOption);

        return fetchPlayers(url, PlayerParser.pitcher);
    }

    private JsonArray fetchPlayers(String url, PlayerParser parser) throws IOException {
        JsonArray players = new JsonArray();
        Elements elements = fetchAndParse(url);

        Iterator<Element> iterator = elements.iterator();
        // skip first item. this is header not data
        iterator.next();

        while(iterator.hasNext()) {
            players.add(parser.parse(iterator.next()).toJson());
        }

        return players;
    }

    private Elements fetchAndParse(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return document.select(".kstats-player-table tbody tr");
    }

}
