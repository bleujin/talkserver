package net.ion.talk.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.TreeContext;
import net.ion.talk.FakeWebSocketConnection;
import net.ion.talk.TalkEngine;
import net.ion.talk.UserConnection;
import net.ion.talk.account.Account;
import net.ion.talk.account.Account.Type;
import net.ion.talk.account.AccountHandler;
import net.ion.talk.account.AccountManager;
import net.ion.talk.account.BotAccount;
import net.ion.talk.account.ConnectedUserAccount;
import net.ion.talk.account.DisconnectedAccount;
import net.ion.talk.account.ProxyAccount;
import net.ion.talk.bean.Const;
import net.ion.talk.bean.Const.User;
import net.ion.talk.bot.TestCrakenBase;
import net.ion.talk.responsebuilder.TalkResponse;

import org.infinispan.atomic.AtomicHashMap;
import org.infinispan.atomic.AtomicMap;
import org.restlet.Context;
import org.restlet.routing.VirtualHost;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 4. 16.
 * Time: 오전 10:30
 * To change this template use File | Settings | File Templates.
 */
public class TestAccountManager extends TestCrakenBase{

    private AccountManager am;
    private TalkEngine tengine;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TreeContext context = TreeContext.createRootContext(new VirtualHost(new Context())) ;
        context.putAttribute(RepositoryEntry.EntryName, rentry) ;
        context.putAttribute(ScheduledExecutorService.class.getCanonicalName(), Executors.newScheduledThreadPool(2)) ;
        tengine = TalkEngine.testCreate(context).init().startEngine() ;
        am = tengine.context().getAttributeObject(AccountManager.class.getCanonicalName(), AccountManager.class) ;
        
        rsession.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/users/ryun").property(User.UserId, "ryun") ;
				return null;
			}
		}) ;
    }
    
    @Override
    public void tearDown() throws Exception {
    	tengine.stopEngine(); 
    	super.tearDown();
    }

    public void testIsDisconnectedUser(){
        String user = "ryun";
        Account account = am.newAccount(user);
        assertTrue(account instanceof DisconnectedAccount);
    }

    public void testIsConnectedUser() throws Exception {
        String user = "ryun";
        tengine.onOpen(FakeWebSocketConnection.create("ryun"));
        Account account = am.newAccount(user);
        assertTrue(account instanceof ProxyAccount);
    }
    
    public void testAppUser() throws Exception {
        String user = "ryun";
        tengine.onOpen(FakeWebSocketConnection.createFromApp("ryun"));
        Account account = am.newAccount(user);
        assertTrue(account instanceof ConnectedUserAccount);
	}
    
    public void testIsNotFoundUser(){
        String user = "notfound";
        Account account = am.newAccount(user);
        assertEquals(Account.Type.NOT_REGISTERED, account.type());
    }

    public void testIsBot() throws Exception {
        Account account = am.newAccount("echo");
        assertTrue(account instanceof BotAccount);
    }

    public void testSendToConnectedUser() throws Exception {
        String user = "ryun";
        String notifyId = "1234";

        FakeWebSocketConnection fake = FakeWebSocketConnection.create("ryun");
		tengine.onOpen(fake);
        Account account = am.newAccount(user);
        TalkResponse notifyMsg = createNotify(user, notifyId);
        account.onMessage(notifyId, new AtomicHashMap<PropertyId, PropertyValue>());
        
        assertEquals(notifyMsg.toString(), fake.recentMsg());
    }

    public void testSendToDisconnectedUser() throws Exception {
        final AtomicReference<String> recevied = new AtomicReference<String>() ;
        am.defineHandle(Type.DISCONNECTED_USER, new AccountHandler() {
			@Override
			public Account create(AccountManager am, String userId, UserConnection uconn) {
				return new Account("ryun", Type.DISCONNECTED_USER){
					@Override
					public void onMessage(String notifyId, AtomicMap<PropertyId, PropertyValue> pmap) {
						recevied.set(notifyId);
					}};
			}
		}) ;

    	String notifyId = "1234";

        Account account = am.newAccount("ryun");
        TalkResponse notifyMsg = createNotify("ryun", notifyId);
        account.onMessage(notifyId, new AtomicHashMap<PropertyId, PropertyValue>());

        assertEquals("1234", recevied.get());

    }

    public void xtestSendToBot() throws Exception {
        String bot = "echoBot";
        String notifyId = "1234";
        createBotToCraken(bot, "http://localhost:9000", false);

        Account account = am.newAccount(bot);

        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/notifies/echoBot/1234").refTo(Const.Message.Message, "/rooms/test/testMessage");
                wsession.pathBy("/rooms/test/testMessage").property(Const.Message.MessageId, "testMessage")
                        .property(Const.Message.Message, "Hello");
                return null;
            }
        });

        TalkResponse notifyMsg = createNotify(bot, notifyId);
        account.onMessage(notifyId, new AtomicHashMap<PropertyId, PropertyValue>());

    }


}
