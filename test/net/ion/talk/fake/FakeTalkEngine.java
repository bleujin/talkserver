package net.ion.talk.fake;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.Aradon;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 16.
 * Time: 오후 5:12
 * To change this template use File | Settings | File Templates.
 */
public class FakeTalkEngine extends TalkEngine {

    private final Map<String, UserConnection> connMap = MapUtil.newMap();
    private final Aradon aradon;

    public FakeTalkEngine(RepositoryEntry rentry, Aradon aradon) throws Exception {
        super(aradon);
        this.aradon = aradon;
        context().putAttribute(RepositoryEntry.EntryName, rentry);
        context().putAttribute(NewClient.class.getCanonicalName(), NewClient.create()
        );
    }

    public FakeUserConnection addConnectedUser(String user) {
        FakeUserConnection uconn = new FakeUserConnection(FakeWebSocketConnection.create(user));
        connMap.put(user, uconn);
        return uconn;
    }

    @Override
    public UserConnection findConnection(String id) {
        return connMap.get(id);
    }
}

