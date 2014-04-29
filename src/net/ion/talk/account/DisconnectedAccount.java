package net.ion.talk.account;

import java.util.concurrent.ExecutionException;

import javapns.notification.PushedNotifications;
import net.ion.craken.node.ReadSession;
import net.ion.message.push.sender.AppleMessage;
import net.ion.message.push.sender.GoogleMessage;
import net.ion.message.push.sender.Pusher;
import net.ion.message.push.sender.handler.PushResponseHandler;
import net.ion.talk.responsebuilder.TalkResponse;

import com.google.android.gcm.server.Result;


/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 5:04
 * To change this template use File | Settings | File Templates.
 */
public class DisconnectedAccount extends Account {

    private final Pusher pusher;
    private final ReadSession rsession;

    DisconnectedAccount(String userId, ReadSession rsession, Pusher pusher) {
        super(userId, Type.DisconnectedUser);
        this.pusher = pusher;
        this.rsession = rsession;
    }

    @Override
    public void onMessage(final String notifyId, TalkResponse response)  {
        pusher.sendTo(accountId()).sendAsync(response.pushMessage(), new PushResponseHandler<Boolean>() {
			@Override
			public Boolean onAPNSSuccess(AppleMessage amsg, PushedNotifications results) {
//                rsession.tran(new TransactionJob<Object>() {
//                    @Override
//                    public Object handle(WriteSession wsession) throws Exception {
//                        wsession.pathBy("/notifies/" + accountId() + "/" + notifyId).removeSelf();
//                        return null;
//                    }
//                });
                
				return Boolean.TRUE;
			}
			@Override
			public Boolean onAPNSFail(AppleMessage amsg, PushedNotifications results) {
				return Boolean.FALSE;
			}
			@Override
			public Boolean onAPNSThrow(AppleMessage amsg, Exception ex) {
				ex.printStackTrace();
				return Boolean.FALSE;
			}
			@Override
			public Boolean onGoogleSuccess(GoogleMessage gmsg, Result result) {
//              rsession.tran(new TransactionJob<Object>() {
//              @Override
//              public Object handle(WriteSession wsession) throws Exception {
//                  wsession.pathBy("/notifies/" + accountId() + "/" + notifyId).removeSelf();
//                  return null;
//              }
//          });				
				return Boolean.TRUE;
			}
			@Override
			public Boolean onGoogleFail(GoogleMessage gmsg, Result result) {
				return Boolean.FALSE;
			}
			@Override
			public Boolean onGoogleThrow(GoogleMessage gmsg, Exception ex) {
				ex.printStackTrace();
				return Boolean.FALSE;
			}
        });


    }

    Pusher sender(){
        return pusher;
    }

}
