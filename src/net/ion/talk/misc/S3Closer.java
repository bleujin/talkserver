package net.ion.talk.misc;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;

import org.apache.commons.fileupload.FileItem;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Object;

public class S3Closer extends UploadCloser implements Closeable {

	private AmzS3Service s3Service;
	private String bucketName = "toonfile";
	private S3Helper helper;

	public S3Closer(int limitSizeM, int scheduleMinute, String key, String pwd) throws ServiceException {
		super(limitSizeM, scheduleMinute);
		this.s3Service = new AmzS3Service(key, pwd);
		s3Service.initBucket(bucketName);
		this.helper = new S3Helper(s3Service);
	}

	@Override
	public void handle(File targetFile) {
		try {
			InputStream targetInput = new FileInputStream(targetFile);
			DataInputStream dinput = new DataInputStream(targetInput);
			int size = dinput.readInt();
			byte[] bytes = new byte[size];
			dinput.read(bytes);
			JsonObject jsonProp = JsonObject.fromString(new String(bytes, Charset.forName("UTF-8")));

			helper.save(jsonProp.asString("filePath"), jsonProp, dinput, jsonProp.asString("content-type"));
			targetFile.delete();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		try {
			s3Service.destroySelf();
		} catch (ServiceException e) {
			throw new IOException(e);
		}
	}

}


