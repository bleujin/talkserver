package net.ion.talkserver;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import net.ion.framework.util.IOUtil;
import net.ion.radon.Options;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.talk.ToonServer;
import net.ion.talkserver.config.TalkConfig;
import net.ion.talkserver.config.builder.ConfigBuilder;

public class Main {

	public static void main(String[] args) throws Exception {

		Options options = new Options(args);
		TalkConfig nsconfig = ConfigBuilder.create(options.getString("config", "./resource/config/talk-config.xml")).build().addTestUser();

		try {
			Socket s = new Socket(InetAddress.getLocalHost(), nsconfig.serverConfig().port());
			s.setSoTimeout(400);
			IOUtil.closeQuietly(s);

			// if connected
			NewClient nc = NewClient.create();
			try {
				ListenableFuture<Response> future = nc.prepareGet("http://localhost:" + nsconfig.serverConfig().port() + "/admin/misc/shutdown?time=10&password=" + nsconfig.serverConfig().password()).execute();
				future.get();
				nc.close();
				Thread.sleep(1000);
			} finally {
				nc.close();
			}

		} catch (ConnectException ex) {
			;
		} 
		
		
		final ToonServer tserver = ToonServer.create(nsconfig);
		tserver.ready().startRadon();
		
		tserver.talkEngine().context().putAttribute(ToonServer.class.getCanonicalName(), tserver) ;
		
		
        Runtime.getRuntime().addShutdownHook(new Thread(){
        	public void run(){
        		try {
					tserver.stop();
				} catch (IllegalStateException e) {
					System.err.println(e.getMessage());
				} catch (Exception e) {
					System.err.println(e.getMessage());
				} 
        	}
        });
		
	}
}
