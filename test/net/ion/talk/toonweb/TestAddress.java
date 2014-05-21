package net.ion.talk.toonweb;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.restlet.resource.Get;

import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.PathParam;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.util.AradonTester;
import net.ion.talk.handler.engine.ServerHandler;
import net.ion.talk.util.NetworkUtil;
import junit.framework.TestCase;

public class TestAddress extends TestCase {

	public void testAtAddress() throws Exception {
		Aradon aradon = AradonTester.create().register("bot", "/{botId}/{pwd}", EchoLet.class).getAradon();
		Radon radon = aradon.toRadon(9000).start().get();

		NewClient nc = NewClient.create();

		Response response = nc.prepareGet(NetworkUtil.httpAddress(9000, "/bot/bleujin@i-on.net/123456")).execute().get();

		Debug.line(response.getTextBody());

		nc.close();
		radon.stop().get();
	}

	public void testHostName() throws Exception {
		InetAddress address = InetAddress.getLocalHost();
		Debug.line(address.getHostAddress(), address.getHostAddress(), 9000);

		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				Debug.debug(i.getHostAddress(), i.getAddress(), i.getCanonicalHostName());
			}
		}
		
		
	}

	
	public void testLocalPublicIp() throws Exception {
		Debug.line(getCurrentEnvironmentNetworkIp());
	}
	
	public static String getCurrentEnvironmentNetworkIp(){
        Enumeration netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return getLocalIp();
        }

        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface)netInterfaces.nextElement();
            Enumeration address = ni.getInetAddresses();
            if (address == null) {
                return getLocalIp();
            }
            while (address.hasMoreElements()) {
                InetAddress addr = (InetAddress)address.nextElement();
                if (!addr.isLoopbackAddress() && !addr.isSiteLocalAddress() && !addr.isAnyLocalAddress() ) {
                    String ip = addr.getHostAddress();
                    if( ip.indexOf(".") != -1 && ip.indexOf(":") == -1 ){
                        return ip;
                    }
                }
            }
        }
        return getLocalIp();
    }
	
	public static String getLocalIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

}




class EchoLet implements IServiceLet {

	@Get
	public String echo(@AnRequest InnerRequest request, @PathParam("botId") String botId, @PathParam("pwd") String pwd) {
		return botId + "/" + pwd;
	}
}