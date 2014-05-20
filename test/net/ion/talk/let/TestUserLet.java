package net.ion.talk.let;

import junit.framework.TestCase;
import net.ion.craken.aradon.bean.RepositoryEntry;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.util.Debug;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.representation.JsonObjectRepresentation;
import net.ion.radon.util.AradonTester;
import net.ion.talk.let.UserLet;

public class TestUserLet extends TestCase {

	private RepositoryEntry rentry;
	private AradonTester tester;
	private AradonClient client;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		rentry = RepositoryEntry.test();

		tester = AradonTester.create().putAttribute("repository", rentry).register("", "/user/{email}", IMatchMode.STARTWITH, UserLet.class);
		client = AradonClientFactory.create(tester.getAradon());

	}
	
	@Override
	public void tearDown() throws Exception {
		this.rentry.shutdown();
		super.tearDown();
	}

	public void testFirst() throws Exception {
		final String userId = "airkjh@i-on.net";
		createDummyRequest(userId).post();

		ReadSession session = rentry.login();
		assertTrue(session.exists("/users/airkjh@i-on.net"));

		ReadNode node = session.pathBy("/users/airkjh@i-on.net");

		assertEquals("airkjh@i-on.net", node.property("email").asString());
		assertEquals("1", node.property("password").asString());
		assertEquals("+82", node.property("country").asString());
		assertEquals("1091399660", node.property("phone").asString());
	}
	
	public void testRegisterExistingUser() {
		createDummyRequest("airkjh@i-on.net").post();
		
		try {
			createDummyRequest("airkjh@i-on.net").post();
		} catch(IllegalArgumentException e) {
			
		}
	}
	
	public void testRetreive() {
		createDummyRequest("airkjh@i-on.net").post();
		
		JsonObjectRepresentation representation = (JsonObjectRepresentation)client.createRequest("/user/airkjh@i-on.net").get();
		
		Debug.line(representation.getJsonObject());
	}

	private IAradonRequest createDummyRequest(String userId) {
		return client.createRequest("/user/" + userId)
				.addParameter("email", userId)
				.addParameter("password", "1")
				.addParameter("country", "+82")
				.addParameter("phone", "1091399660")
				.addParameter("nickname", "airkjh");
	}
}
