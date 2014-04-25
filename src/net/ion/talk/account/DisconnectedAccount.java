package net.ion.talk.account;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.message.push.sender.PushResponse;
import net.ion.message.push.sender.Pusher;
import net.ion.message.push.sender.handler.ResponseHandler;
import net.ion.talk.responsebuilder.TalkResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 5:04
 * To change this template use File | Settings | File Templates.
 */
public class DisconnectedAccount extends Account {

    private final Pusher sender;
    private final ReadSession rsession;

    DisconnectedAccount(String userId, ReadSession rsession, Pusher sender) {
        super(userId, Type.DisconnectedUser);
        this.sender = sender;
        this.rsession = rsession;
    }

    @Override
    public Object onMessage(final String notifyId, TalkResponse response) throws ExecutionException, InterruptedException {
        return sender.sendTo(accountId()).sendAsync(response.pushMessage(), new ResponseHandler<Object>() {
            @Override
            public <T> T result() {
                return null;
            }

            @Override
            public void onSuccess(PushResponse response) {
                try {
                    rsession.tranSync(new TransactionJob<Object>() {
                        @Override
                        public Object handle(WriteSession wsession) throws Exception {
                            wsession.pathBy("/notifies/" + accountId() + "/" + notifyId).removeSelf();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(PushResponse response) {
            }

            @Override
            public void onThrow(String receiver, String token, Throwable t) {
            }
        });


    }

    Pusher sender(){
        return sender;
    }

}
