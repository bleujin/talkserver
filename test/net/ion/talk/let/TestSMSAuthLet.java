package net.ion.talk.let;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import org.restlet.Response;
import org.restlet.data.Method;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 3. 21.
 * Time: 오후 1:29
 * To change this template use File | Settings | File Templates.
 */
public class TestSMSAuthLet extends TestBaseLet{

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tserver.startRadon();
    }

    public void testFirst() throws Exception {

        Response response = tserver.mockClient().fake().createRequest("/register/SMSAuth").addParameter("phone", "01025704848").handle(Method.POST);
        assertEquals(200, response.getStatus().getCode());

        String result = JsonObject.fromString(response.getEntityAsText()).asString("result");
        assertEquals("인증번호를 발송하였습니다.", result);

    }

    public void testRandomCode(){
        Debug.line(RandomUtil.nextInt(999999)+1);
    }
}
