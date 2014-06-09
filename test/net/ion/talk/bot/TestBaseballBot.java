package net.ion.talk.bot;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.util.Debug;
import net.ion.talk.bot.baseball.Player;
import net.ion.talk.bot.baseball.PlayerRanker;

import java.io.IOException;
import java.util.List;

public class TestBaseballBot extends TestCase {

    public void testTop10Hitter() throws IOException {
        PlayerRanker ranker = PlayerRanker.create();
        JsonArray hitters = ranker.getTopNHitter(10, "AVG", "asc");

        assertEquals(10, hitters.size());
        Debug.line(hitters);
    }

    public void testTop10Pitcher() throws IOException {
        PlayerRanker ranker = PlayerRanker.create();
        JsonArray hitters = ranker.getTopNPitcher(10, "ERA", "asc");

        assertEquals(10, hitters.size());
        Debug.line(hitters);
    }

}




