package net.ion.ryun;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.util.uriparser.URIPattern;
import net.ion.radon.util.uriparser.URIResolveResult;
import net.ion.radon.util.uriparser.URIResolver;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2014. 2. 14.
 * Time: 오후 5:03
 * To change this template use File | Settings | File Templates.
 */
public class TestMatch extends TestCase {

    private String pattern = "ROOM|{id}|{COMMAND}|{user}";
    private String message = "ROOM|1|ENTER|ryun";

    public void testMatch() throws Exception {


        assertTrue(new URIPattern(pattern).match(message));
        Debug.line(resolve(pattern, message));

    }

    public Map<String, String> resolve(String pattern, String message){
        URIResolveResult resolver = new URIResolver(message).resolve(new URIPattern(pattern));
        Map<String, String> result = MapUtil.newMap() ;

        for(String name : resolver.names()){
            result.put(name, ObjectUtil.toString(resolver.get(name))) ;
        }

        return result ;
    }
}
