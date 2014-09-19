package net.ion.craken.aradon;

import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;

public class TestNodeLet extends TestCrakenBase {

    private NodeLet let ;
    
    @Override
    public void setUp() throws Exception {
    	super.setUp();
    	this.let = new NodeLet(RepositoryEntry.test()); ;
    }
    

    public void testFirst() throws Exception {

        let.upsertNode(session, "/bleujin", "{'name':'airkjh', 'int': 1, 'long': 3, 'boolean': false, 'arrStr':['a','b','c'], 'arrInt': [1,2,3]}");

        ReadNode node = session.pathBy("/bleujin");
        assertEquals("airkjh", node.property("name").stringValue());
        assertEquals(1, node.property("int").intValue(0));
        assertEquals(3L, node.property("long").longValue(0));
        assertEquals(true, node.property("boolean").value() == Boolean.FALSE);

        assertEquals(3, node.property("arrStr").asSet().size());
        assertEquals("a", node.property("arrStr").stringValue());

        assertEquals(3, node.property("arrInt").asSet().size());
        assertEquals(1, node.property("arrInt").intValue(0));
    }

    public void testArrayUpdateNotAppend() throws Exception {
        let.upsertNode(session, "/airkjh", "{'a':[1,2,3]}");
        let.upsertNode(session, "/airkjh", "{'a':[4,5,6]}");

        ReadNode node = session.pathBy("/airkjh");
        assertEquals(3, node.property("a").asSet().size());
        assertEquals(4, node.property("a").intValue(0));
    }

    public void testInvalid() {
        try {
            // JsonObject as property value is prohibitted
            let.upsertNode(session, "/airkjh", "{'a':{'b':'c'}}");
            fail();
        } catch (Exception e) {
            // success
            Debug.line(e);
        }
    }

    public void testDeleteNode() throws Exception {

        let.upsertNode(session, "/airkjh", "{'a':[1,2,3]}");
        let.deleteNode(session, "/airkjh");

        assertFalse(session.exists("/airkjh"));

    }

    public void testUpsertPropertyOnly() throws Exception {
        session.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/airkjh_prop_update").property("name", "airkjh").property("age", 35);
                return null;
            }
        });

        let.upsertNode(session, "/airkjh_prop_update.name", "{'name':'airkjh2'}");
        ReadNode updatedNode = session.pathBy("/airkjh_prop_update");

        assertEquals("airkjh2", updatedNode.property("name").stringValue());
        assertEquals(35, updatedNode.property("age").intValue(-1));
    }

    public void testDeleteProperty() throws Exception {
        session.tranSync(new TransactionJob<Void>() {
            @Override
            public Void handle(WriteSession wsession) throws Exception {
                wsession.pathBy("/airkjh_prop_delete").property("name", "airkjh").property("age", 35);
                return null;
            }
        });

        let.upsertNode(session, "/airkjh_prop_delete", "{'name':'airkjh2'}");
        ReadNode updatedNode = session.pathBy("/airkjh_prop_delete");

        assertEquals("airkjh2", updatedNode.property("name").stringValue());
        assertEquals(false, updatedNode.hasProperty("age"));
    }
}
