package net.ion.talk.let;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.StringPart;
import net.ion.radon.aclient.multipart.FilePart;
import net.ion.radon.core.Aradon;
import net.ion.radon.util.AradonTester;
import net.ion.talk.TalkEngine;

public class TestUploadLet extends TestCase {

	private Radon radon;

	@Override
	protected void setUp() throws Exception {
		Aradon aradon = AradonTester.create().register("upload", "/{userId}/{resource}, /{userId}/{resource}/{fieldname}", UploadLet.class).getAradon();
		this.radon = aradon.toRadon(9000).start().get();
	}

	@Override
	protected void tearDown() throws Exception {
		radon.stop();
		super.tearDown();
	}

	public void testUpload() throws Exception {
		NewClient nc = NewClient.create();
		File file = new File("./resource/temp/한글이름.jpg");

		Response response = nc.preparePost("http://localhost:9000/upload/bleujin/123456")
					.addBodyPart(new FilePart("image", file, "image/jpeg", "UTF-8"))
					.addBodyPart(new FilePart("thumb", file))
					.addBodyPart(new StringPart("name", "한글이름")).setBodyEncoding("UTF-8").execute().get();
		assertEquals(200, response.getStatusCode());
//		Debug.line(response.getUTF8Body());
		nc.close();
	}

	public void testViewInfo() throws Exception {
		testUpload();

		NewClient nc = NewClient.create();
		Response response = nc.prepareGet("http://localhost:9000/upload/bleujin/123456").execute().get();
		assertEquals(200, response.getStatusCode());
		Debug.line(response.getUTF8Body());
		nc.close();
	}

	public void testViewFile() throws Exception {
		NewClient nc = NewClient.create();
		Response response = nc.prepareGet("http://localhost:9000/upload/bleujin/123456/image").execute().get();
		assertEquals(200, response.getStatusCode());
		assertEquals(200, nc.prepareGet("http://localhost:9000/upload/bleujin/123456/thumb").execute().get().getStatusCode());

		Debug.line(response.getContentType(), response.getHeaders());
		File file = new File("./resource/temp/temp.jpg");
		if (file.exists())
			file.delete();
		IOUtil.copyNClose(response.getBodyAsStream(), new FileOutputStream(file));
		nc.close();
	}

	public void testDelete() throws Exception {
		NewClient nc = NewClient.create();
		assertEquals(200, nc.prepareDelete("http://localhost:9000/upload/bleujin/123456/image").execute().get().getStatusCode());
		nc.close();
	}

	public void testEncode() throws Exception {
		String defaultCharset = "ISO-8859-1";
		String str = "한글";

		Debug.line(str.getBytes(defaultCharset));
	}
}
