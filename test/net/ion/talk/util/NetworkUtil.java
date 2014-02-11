package net.ion.talk.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 10.
 * Time: 오후 5:42
 * To change this template use File | Settings | File Templates.
 */
public class NetworkUtil {
    public static String getHostAddress(){

        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }

    }

    public static String getHostAddressWithProtocol(String protocolName){
        return protocolName +"://" + getHostAddress();
    }
}
