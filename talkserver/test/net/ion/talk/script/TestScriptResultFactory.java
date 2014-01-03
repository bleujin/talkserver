package net.ion.talk.script;

import junit.framework.TestCase;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.script.rhino.ResponseHandler;
import net.ion.script.rhino.RhinoEngine;
import net.ion.script.rhino.RhinoScript;

/**
 * Author: Ryunhee Han
 * Date: 2013. 12. 31.
 */
public class TestScriptResultFactory extends TestCase {

    private RepositoryImpl repoImpl;
    private ReadSession session;
    private ScriptResultFactory sresult;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        repoImpl = RepositoryImpl.inmemoryCreateWithTest();
        session = repoImpl.login("test");
        sresult = ScriptResultFactory.create("test");

        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/node/test").property("name", "Ryun").property("age", 24).property("location", "seoul").addChild("childRyun").property("age", 3);
                wsession.pathBy("/node/test2").property("name", "Lime").property("age", 31).property("location", "usa");
                return null;
            }
        });
    }

    @Override
    public void tearDown() throws Exception {
        repoImpl.shutdown();
        super.tearDown();

    }

    public void testAddNode() throws Exception {

        ReadNode node = session.pathBy("/node/test");

        sresult.setRule(ResultRule.PROPERTY_ALL);
        sresult.addNodes("nodes", node);

        JsonObject json = JsonObject.fromString(sresult.make());
        assertTrue(json.has("nodes"));
        assertEquals(1, json.getAsJsonArray("nodes").size());

        JsonObject json0 = json.getAsJsonArray("nodes").get(0).getAsJsonObject();
        assertEquals("Ryun", json0.get("name").getAsString());
        assertEquals(24, json0.get("age").getAsInt());
    }

    public void testAddMultiNode() throws Exception {


        ReadNode node = session.pathBy("/node/test");
        sresult.setRule(ResultRule.PROPERTY_ALL);
        sresult.addNodes("nodes", node, node);

        JsonObject json = JsonObject.fromString(sresult.make());
        assertTrue(json.has("nodes"));
        assertEquals(2, json.getAsJsonArray("nodes").size());

        JsonObject json0 = json.getAsJsonArray("nodes").get(0).getAsJsonObject();
        JsonObject json1 = json.getAsJsonArray("nodes").get(1).getAsJsonObject();

        assertEquals("Ryun", json0.get("name").getAsString());
        assertEquals(24, json0.get("age").getAsInt());
        assertEquals("Ryun", json1.get("name").getAsString());
        assertEquals(24, json1.get("age").getAsInt());
    }

    public void testStaticConstantInRhino() throws Exception {

        String script = "var testNode = session.pathBy('/node/test');  sresult.setRule(PROPERTY_ALL); sresult.addNodes('nodes', testNode); sresult.make();";


        RhinoEngine rengine = RhinoEngine.create().start().get();
        RhinoScript rscript = rengine.newScript("test").defineScript(script).bind("session", session).bind("sresult", sresult).bind("PROPERTY_ALL", ResultRule.PROPERTY_ALL);
        String result = rscript.exec(ResponseHandler.StringMessage);

        JsonObject json = JsonObject.fromString(result);
        assertTrue(json.has("nodes"));
        assertEquals(1, json.getAsJsonArray("nodes").size());

        JsonObject json0 = json.getAsJsonArray("nodes").get(0).getAsJsonObject();
        assertEquals("Ryun", json0.get("name").getAsString());
        assertEquals(24, json0.get("age").getAsInt());
    }


    public void testAddNodeWithIgnore() throws Exception {

        ReadNode node = session.pathBy("/node/test");
        ReadNode node2 = session.pathBy("/node/test2");
        sresult.setRule(ResultRule.IGNORE_PROPERTY);
        sresult.setIgnoreProperty("age", "name");
        sresult.addNodes("nodes", node);
        sresult.setRule(ResultRule.PROPERTY_ALL);
        sresult.addNodes("nodes", node2);

        JsonObject json = JsonObject.fromString(sresult.make());
        assertTrue(json.has("nodes"));
        assertEquals(2, json.getAsJsonArray("nodes").size());

        JsonObject json0 = json.getAsJsonArray("nodes").get(0).getAsJsonObject();
        JsonObject json1 = json.getAsJsonArray("nodes").get(1).getAsJsonObject();

        assertFalse(json0.has("name"));
        assertFalse(json0.has("age"));
        assertEquals("Lime", json1.get("name").getAsString());
        assertEquals(31, json1.get("age").getAsInt());
    }

}
