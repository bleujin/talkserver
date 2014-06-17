package net.ion.talk.account;

import org.infinispan.atomic.AtomicMap;

import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.StringUtil;
import net.ion.talk.UserConnection;
import net.ion.talk.bean.Const;
import net.ion.talk.responsebuilder.TalkResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 20.
 * Time: 오후 4:38
 * To change this template use File | Settings | File Templates.
 */
public class ConnectedUserAccount extends Account {

    private final UserConnection uconn;
    private final ReadSession rsession;

    ConnectedUserAccount(String userId, ReadSession rsession, UserConnection uconn) {
        super(userId, Type.CONNECTED_USER);
        this.uconn = uconn;
        this.rsession = rsession;
    }

    @Override
    public void onMessage(final String notifyId, AtomicMap<PropertyId, PropertyValue> pmap) {
        uconn.sendMessage(new JsonObject()
        			.put("notifyId", notifyId)
        			.put("result", new JsonObject()
									.put(Const.Room.RoomId,  StringUtil.substringAfterLast(pmap.get(PropertyId.refer(Const.Room.RoomId)).asString(), "/"))
        							.put(Const.Message.MessageId,  notifyId)
        							.put(Const.Message.Sender,  pmap.get(PropertyId.normal(Const.Notify.SenderId)).asString())
        							.put(Const.Notify.SVGUrl, pmap.get(PropertyId.normal(Const.Notify.SVGUrl)).asString()))
        			.toString());
    }

    public UserConnection userConnection(){
        return uconn;
    }


}
