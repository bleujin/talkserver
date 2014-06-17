package net.ion.talk.account;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.infinispan.atomic.AtomicMap;

import javapns.notification.PushedNotifications;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.message.push.sender.AppleMessage;
import net.ion.message.push.sender.GoogleMessage;
import net.ion.message.push.sender.Pusher;
import net.ion.message.push.sender.handler.PushResponseHandler;
import net.ion.talk.bean.Const;
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
    private Logger logger = LogBroker.getLogger(DisconnectedAccount.class) ;

    DisconnectedAccount(String userId, ReadSession rsession, Pusher pusher) {
        super(userId, Type.DISCONNECTED_USER);
        this.pusher = pusher;
        this.rsession = rsession;
    }

    @Override
    public void onMessage(final String notifyId, AtomicMap<PropertyId, PropertyValue> pmap)  {
    	String message = new JsonObject()
    			.put("notifyId", notifyId)
    			.put("result", new JsonObject().put(Const.Room.RoomId,   StringUtil.substringAfterLast(pmap.get(PropertyId.refer(Const.Room.RoomId)).asString(), "/"))
    							.put(Const.Message.MessageId,  notifyId)
       							.put(Const.Message.Sender,  pmap.get(PropertyId.normal(Const.Notify.SenderId)).asString())
    							.put(Const.Notify.SVGUrl, pmap.get(PropertyId.normal(Const.Notify.SVGUrl)).asString()))
    				.toString() ;
        Future<Boolean> result = pusher.sendTo(accountId()).sendAsync(message, new PushResponseHandler<Boolean>() {
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
				logger.warning(accountId() + ":"+ results.getFailedNotifications().toString());
				return Boolean.FALSE;
			}
			@Override
			public Boolean onAPNSThrow(AppleMessage amsg, Exception ex) {
				logger.severe(accountId() + ":"+ ex.getMessage());
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
				logger.warning(accountId() + ":"+ gmsg.message() + result.toString());
				return Boolean.FALSE;
			}
			@Override
			public Boolean onGoogleThrow(GoogleMessage gmsg, Exception ex) {
				logger.severe(accountId() + ":"+ gmsg.message() + ex.getMessage());
				return Boolean.FALSE;
			}
        });
        
    }

    Pusher sender(){
        return pusher;
    }

}
