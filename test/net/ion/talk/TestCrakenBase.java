package net.ion.talk;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.*;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;
import net.ion.talk.responsebuilder.TalkResponseBuilder;

/**
 * Created with IntelliJ IDEA. User: Ryun Date: 2014. 2. 3. Time: 오후 6:30 To change this template use File | Settings | File Templates.
 */
public class TestCrakenBase extends TestCase {

	protected ReadSession rsession;
    protected RepositoryEntry rentry;

    @Override
	public void setUp() throws Exception {
		super.setUp();
		rentry = RepositoryEntry.test();
        rentry.start();
		rsession = rentry.login();
	}

	@Override
	public void tearDown() throws Exception {
        rentry.shutdown();
		super.tearDown();
	}


    protected ReadNode getFirstNotify(String userId) {
        return rsession.pathBy("/notifies/"+userId).children().firstNode();
    }

    protected void sendWhisperMessage(final String roomId, final String messageId, final String sender, final String msg, final String ... receivers){
        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                WriteNode message = wsession.pathBy("/rooms/"+roomId+ "/messages/"+messageId)
                        .refTo(Const.Message.Sender, "/users/" + sender)
                        .property(Const.Message.Message, msg)
                        .property(Const.Message.MessageId, messageId);
                for(String receiver : receivers){
                    message.append(Const.Message.Receivers, receiver);
                }
                return null;
            }
        });
    }

    protected void sendMessageToRoom(String roomId, String messageId, String sender, String msg) {
        sendWhisperMessage(roomId, messageId, sender, msg, new String[0]);
    }


    protected void createBotToCraken(final String botId, final String requestURL, final boolean isSync) {
        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/"+botId).property("requestURL", requestURL).property(Const.Bot.isSyncBot, isSync);
                return null;
            }
        });
    }

    protected void createUserToCraken(final String user) {
        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/users/" + user);
                return null;
            }
        });
    }

    protected void addUserToRoom(final String roomId, final String ... users){
        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                for(String userId : users){
                    wsession.pathBy("/rooms/"+roomId+ "/members").child(userId).refTo(Const.Ref.User, "/users/" + userId);
                }
                return null;
            }
        });
    }

    protected void removeUserToRoom(final String roomId, final String ... users){
        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                for(String userId : users){
                    wsession.pathBy("/rooms/"+roomId+ "/members/"+userId).removeSelf();
                }
                return null;
            }
        });
    }


    protected TalkResponse createNotify(final String user, final String notifyId){
        rsession.tran(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/notifies/" + user + "/" + notifyId);
                return null;
            }
        });

        return TalkResponseBuilder.create().newInner().property(Const.Notify.NotifyId, notifyId).build();
    }
}
