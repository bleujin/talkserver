package net.ion.talk.util;

import java.util.GregorianCalendar;

public class CalUtil {

	public final static long gmtTime(){
		return GregorianCalendar.getInstance().getTimeInMillis() ;
	}
}
