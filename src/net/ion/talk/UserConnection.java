package net.ion.talk;

import java.util.Collections;
import java.util.Map;

import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.WebSocketConnection;
import net.ion.talk.TalkEngine.Reason;
import net.ion.talk.util.CalUtil;

public class UserConnection {

	public static final UserConnection NOTFOUND = new UserConnection(null);
	
	private WebSocketConnection inner;

    protected UserConnection(WebSocketConnection inner) {
		this.inner = inner ;
	}

	static UserConnection create(WebSocketConnection conn) {
		return new UserConnection(conn) ;
	}

	
	public String id(){
		return inner.getString("id") ;
	}

    public String accessToken(){
        return inner.getString("accessToken");
    }

	public String asString(String key){
		return ObjectUtil.toString(inner.data(key)) ;
	}

	public Object asObject(String key){
		return inner.data(key) ;
	}
	
	public Map<String, Object> asMap(){
		return Collections.unmodifiableMap(inner.data()) ;
	}

	public void sendMessage(String message) {
		inner.send(message) ;
	}

	void close(Reason reason) {
		inner.close() ;
	}

	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof UserConnection){
			return ((UserConnection)obj).inner.equals(this.inner) ;
		}
		return false ;
	}
	
	@Override
	public int hashCode(){
		return this.id().hashCode() ;
	}
	
	public String toString(){
		return "UserConnection[" + id() + "/" + accessToken() + "]" ;
	}

	public boolean isAllowUser(String accessToken) {
        return accessToken().equals(accessToken);
    }

    public void updateHeartBeat() {
    	inner.data("_sessionTime", CalUtil.gmtTime()) ;
    }
    
    public long sessionTime(){
    	return (Long) inner.data("_sessionTime") ;
    }
    
}
