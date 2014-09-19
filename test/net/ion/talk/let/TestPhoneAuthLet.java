package net.ion.talk.let;

import java.util.List;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.let.PathHandler;
import net.ion.talk.util.NetworkUtil;

public class TestPhoneAuthLet extends TestCase {
	
    private RepositoryEntry repoEntry;
	private Radon radon;

	@Override
    public void setUp() throws Exception {
        super.setUp();
		this.repoEntry = RepositoryEntry.test();
		SMSSender smsSender = SMSSender.create(NewClient.create());

		radon = RadonConfiguration.newBuilder(9000)
			.add(new PathHandler(PhoneAuthLet.class).prefixURI("/register")).startRadon() ;
		
		radon.getConfig().getServiceContext().putAttribute(RepositoryEntry.EntryName, repoEntry) ;
		radon.getConfig().getServiceContext().putAttribute(SMSSender.class.getCanonicalName(), smsSender) ;
    }

	@Override
	public void tearDown() throws Exception {
		repoEntry.shutdown();
		radon.stop().get() ;
		super.tearDown();
	}	
	
	public void testRequest() throws Exception {
    	NewClient nc = NewClient.create() ;
        net.ion.radon.aclient.Response response = nc.preparePost(NetworkUtil.httpAddress(9000, "/register/SMSAuth")).addParameter("phone", "+821042216492").execute().get();
        assertEquals(200, response.getStatus().getCode());

        Debug.line(response.getTextBody());

        nc.close();
        
        ReadSession session = repoEntry.login();
        List<ReadNode> children = session.pathBy("/auth/sms/").children().toList();
        Debug.line(children);
		assertEquals(1, children.size());
	}
}
