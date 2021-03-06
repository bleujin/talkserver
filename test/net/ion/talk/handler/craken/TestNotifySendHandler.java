package net.ion.talk.handler.craken;



import java.io.IOException;

import junit.framework.TestCase;
import net.ion.craken.node.crud.TreeNodeKey;
import net.ion.craken.tree.Fqn;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.talk.account.Account;
import net.ion.talk.account.AccountManager;
import net.ion.talk.account.EventMap;
import net.ion.talk.bean.Const;
import net.ion.talk.fake.FakeEvent;

import org.infinispan.atomic.AtomicHashMap;
import org.infinispan.notifications.cachelistener.event.Event;

public class TestNotifySendHandler extends TestCase {

    public void testFirst() throws Exception {

        String userId = "ryun";
        String notifyId = "testNotify";

        FakeAccountManager am = new FakeAccountManager();
        NotificationListener listener = new NotificationListener(am);
        listener.memberId = "testWS";

        AtomicHashMap<PropertyId, PropertyValue> value = new AtomicHashMap<PropertyId, PropertyValue>();
        value.put(PropertyId.fromIdString(Const.Connection.DelegateServer), PropertyValue.createPrimitive("testWS"));

        FakeEvent fakeEvent = new FakeEvent();
        fakeEvent.setKey(new TreeNodeKey(Fqn.fromString("/notifies/"+userId+"/"+notifyId), TreeNodeKey.Type.DATA));
        fakeEvent.setPre(false);
        fakeEvent.setType(Event.Type.CACHE_ENTRY_MODIFIED);
        fakeEvent.setValue(value);

        listener.modified(fakeEvent);

        assertEquals(userId, am.userId);
        assertEquals(notifyId, am.notifyId);

    }

    private class FakeAccountManager extends AccountManager {
        public String notifyId;
        public String userId;

        public FakeAccountManager() throws Exception {
            super(null, null, null);
        }

        @Override
        protected void init() throws IOException {
        }

        @Override
        public Account newAccount(String userId) {

            this.userId = userId;

            return new Account(userId, null) {
                @Override
                public void onMessage(String notifyId, EventMap pmap) {
                    FakeAccountManager.this.notifyId = notifyId;
                }
            };
        }
    }
}
