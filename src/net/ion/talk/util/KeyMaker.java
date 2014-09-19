package net.ion.talk.util;

import java.security.InvalidKeyException;
import java.util.Date;

import net.ion.framework.util.HashFunction;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.helpers.Hex;
import net.ion.radon.util.csv.CipherUtil;

public class KeyMaker {

	private final int expireAfterSec ;
	private final String stmt ;
	
	public KeyMaker(){
		this(System.getProperty("net.ion.talk.KeyMaker"), 30) ;
	}
	
	public KeyMaker(String stmt, int expireAfterSec){
		this.stmt = StringUtil.defaultIfEmpty(stmt, "this is key statement") ;
		this.expireAfterSec = expireAfterSec * 1000 ;
	}

	public long publishKey(String userId) {
		try {
			return makeKey(userId, expireAfterSec);
		} catch (InvalidKeyException e) {
			return 0L ;
		}
	}

	
	private long makeKey(String factor, int expireMili) throws InvalidKeyException {
		long time = new Date().getTime() / expireMili;
		
		byte[] encrypted = CipherUtil.encrypt(stmt, factor);
		return Math.abs(HashFunction.hashGeneral(Hex.toHex(encrypted)) / 10 + time); // "/10" -> for long overflow
	}

	public boolean isAuthorized(String factor, long publishedKey) {
		try {
			long conKey = makeKey(factor, expireAfterSec);
			return Math.abs(conKey - publishedKey) <= 1;
		} catch (InvalidKeyException e) {
			return false ;
		}
	}
}
