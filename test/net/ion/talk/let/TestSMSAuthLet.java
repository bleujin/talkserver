package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.message.sms.sender.SMSSender;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;
import net.ion.talk.util.NetworkUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 21.
 * Time: 오후 1:29
 * To change this template use File | Settings | File Templates.
 */
public class TestSMSAuthLet extends TestCase{

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
					.path("smsAuth").addUrlPattern("/SMSAuth").matchMode(IMatchMode.STARTWITH).handler(SMSAuthLet.class).build() ;
		radon = Aradon.create(configuration).toRadon() ;
		radon.start().get() ;
    }
	
	@Override
	public void tearDown() throws Exception {
		repoEntry.shutdown();
		radon.stop().get() ;
		super.tearDown();
	}

    public void testSendAuthNo() throws Exception {
    	NewClient nc = NewClient.create() ;
        net.ion.radon.aclient.Response response = nc.preparePost(NetworkUtil.httpAddress(9000, "/register/SMSAuth")).addParameter("phone", "01042216492").execute().get();
        assertEquals(200, response.getStatus().getCode());

        String result = JsonObject.fromString(response.getTextBody()).asString("result");
        assertEquals("인증번호를 발송하였습니다.", result);

        nc.close(); 
    }

    public void xtestRandomCode(){
        Debug.line(RandomUtil.nextInt(999999)+1);
    }
}
