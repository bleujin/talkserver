package net.ion.talk.handler.craken;

import java.io.IOException;
import java.util.Map;

import net.ion.craken.listener.WorkspaceListener;
import net.ion.craken.node.Workspace;
import net.ion.craken.node.crud.TreeNodeKey;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.talk.account.AccountManager;
import net.ion.talk.account.EventMap;
import net.ion.talk.bean.Const.Connection;

import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

@Listener(sync=false)
public class NotificationListener implements WorkspaceListener{

    private AccountManager am;
	protected String memberId;

    public NotificationListener(AccountManager am) throws IOException {
        this.am = am;
	}

	@CacheEntryModified
	public void modified(CacheEntryModifiedEvent<TreeNodeKey, AtomicMap<PropertyId, PropertyValue>> event) {

		if (event.isPre()) return ;
		if (! event.getKey().getType().isData()) return ;
		
		String pattern = "/notifies/{userId}/{notifyId}";
		if (event.getKey().getFqn().isPattern(pattern)){
			
			Map<String, String> resolveMap = event.getKey().getFqn().resolve(pattern) ;
			final String userId = resolveMap.get("userId");
			final String notifyId = resolveMap.get("notifyId");

			EventMap emap = EventMap.create(event) ;
			PropertyValue pvalue = emap.idString(Connection.DelegateServer) ;
			
			if(pvalue != null && pvalue.stringValue().equals(this.memberId)){
                am.newAccount(userId).onMessage(notifyId, emap);
            }
		}
	}

	@Override
	public void registered(Workspace wspace) {
		this.memberId = wspace.repository().memberId() ;

    }

	@Override
	public void unRegistered(Workspace wspace) {
	}
}
