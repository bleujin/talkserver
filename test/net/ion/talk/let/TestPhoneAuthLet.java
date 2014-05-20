package net.ion.talk.let;

import java.util.List;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.ReadChildren;
import net.ion.framework.util.Debug;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.talk.util.NetworkUtil;

public class TestPhoneAuthLet extends TestCase {
	
    private RepositoryEntry repoEntry;
	private Radon radon;

	@Override
    public void setUp() throws Exception {
        super.setUp();
		this.repoEntry = RepositoryEntry.test();
		SMSSender smsSender = SMSSender.create(NewClient.create());

		Configuration configuration = ConfigurationBuilder.newBuilder()	
			.aradon()
				.addAttribute(RepositoryEntry.EntryName, repoEntry)
				.addAttribute(SMSSender.class.getCanonicalName(), smsSender)
			.sections()
				.restSection("register")
					.path("phoneAuth").addUrlPattern("/SMSAuth").matchMode(IMatchMode.STARTWITH).handler(PhoneAuthLet.class).build() ;
		
		radon = Aradon.create(configuration).toRadon() ;
		radon.start().get() ;
    }

	@Override
	public void tearDown() throws Exception {
		repoEntry.shutdown();
		radon.stop().get() ;
		super.tearDown();
	}	
	
	public void testRequest() throws Exception {
    	NewClient nc = NewClient.create() ;
        net.ion.radon.aclient.Response response = nc.preparePost(NetworkUtil.httpAddress(9000, "/register/SMSAuth")).addParameter("phone", "+821091399660").execute().get();
        assertEquals(200, response.getStatus().getCode());

        nc.close();
        
        ReadSession session = repoEntry.login();
        List<ReadNode> children = session.pathBy("/auth/sms/").children().toList();
        Debug.line(children);
		assertEquals(1, children.size());
	}
}
