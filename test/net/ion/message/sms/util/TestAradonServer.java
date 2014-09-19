package net.ion.message.sms.util;

import net.ion.message.sms.callback.ConsoleCallbackLet;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.core.let.PathHandler;

public class TestAradonServer {

	public static void main(String[] args) throws Exception {

		RadonConfiguration.newBuilder(9000)
			.add(new PathHandler(ConsoleCallbackLet.class).prefixURI("/callback")).startRadon() ;
	}

}

