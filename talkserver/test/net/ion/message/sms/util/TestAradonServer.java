package net.ion.message.sms.util;

import net.ion.message.sms.callback.ConsoleCallbackLet;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.config.IPathConfigFactory;
import net.ion.radon.core.config.SectionConfiguration;
import net.ion.radon.util.AradonTester;

public class TestAradonServer {

    public static void main(String[] args) throws Exception {
        Aradon aradon = AradonTester.create().getAradon();


        SectionService section = aradon.attach(SectionConfiguration.createBlank("callback"));
        section.attach(IPathConfigFactory.create("", "/receive", "", EnumClass.IMatchMode.EQUALS, ConsoleCallbackLet.class));

        aradon.startServer(9000);
    }

}
