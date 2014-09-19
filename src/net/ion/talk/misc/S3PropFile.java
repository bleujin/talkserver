package net.ion.talk.misc;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Object;

public class S3PropFile {

	private S3Object s3o;
	private String jsonProp;

	public S3PropFile(S3Object s3o) throws ServiceException, IOException {
		this.s3o = s3o;
		initProp();
	}

	private void initProp() throws ServiceException, IOException {
		DataInputStream input = new DataInputStream(s3o.getDataInputStream());

		int size = input.readInt();
		byte[] bytes = new byte[size];
		input.read(bytes);

		this.jsonProp = new String(bytes, Charset.forName("UTF-8"));
	}

	public String filePath() {
		return s3o.getName();
	}

	public InputStream inputStream() throws ServiceException {
		return s3o.getDataInputStream();
	}

	public String propJson() {
		return jsonProp;
	}

	public boolean exist() {
		return true;
	}

}
