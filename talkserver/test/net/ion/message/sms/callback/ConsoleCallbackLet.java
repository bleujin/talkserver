package net.ion.message.sms.callback;

import net.ion.framework.util.Debug;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.core.let.MultiValueMap;
import org.restlet.resource.Post;

public class ConsoleCallbackLet extends AbstractServerResource {

    @Post
    public String printToConsole() {
        MultiValueMap formParameter = getInnerRequest().getFormParameter();
        Debug.line(formParameter);
        return formParameter.toString() ;
    }


}
