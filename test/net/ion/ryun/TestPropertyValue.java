package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.listener.CDDHandler;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNodeKey;
import net.ion.framework.util.Debug;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 25. Time: 오후 2:26 To change this template use File | Settings | File Templates.
 */
public class TestPropertyValue extends TestCase {

	private RepositoryEntry rentry;
	private ReadSession rsession;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		rentry = RepositoryEntry.test();
		rsession = rentry.login();
	}

	public void testFirst() throws Exception {

		rsession.tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/test/ryun").append("receivers", "alex", "lucy", "steve");
				return null;
			}
		});

		Set receivers = rsession.pathBy("/test/ryun").property("receivers").asSet();
		Debug.line(receivers);

		rsession.tranSync(new TransactionJob<Object>() {
			@Override
			public Object handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/test/ryun").unset("receivers");
				return null;
			}
		});

		PropertyValue pValue = rsession.pathBy("/test/ryun").property("receivers");
		assertEquals(pValue, PropertyValue.NotFound);

	}

	@Override
	public void tearDown() throws Exception {
		rentry.shutdown();
		super.tearDown();
	}

}
