package net.ion.talk.let;

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
import org.restlet.service.MetadataService;

public class S3Closer extends UploadCloser implements Closeable {

	private AmzS3Service s3Service;
	private String bucketName = "toonfile";
	private S3Helper helper;
	private MetadataService mservice = new MetadataService();

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

class S3PropFile {

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

class S3Helper {

	private AmzS3Service as3;

	public S3Helper(AmzS3Service as3) {
		this.as3 = as3;
	}

	public List<S3PropFile> listFile(String resourcePath) throws ServiceException, IOException {
		S3Object[] s3os = as3.listBy(resourcePath);
		List<S3PropFile> result = ListUtil.newList();
		for (S3Object s3o : s3os) {
			if (!s3o.isDirectoryPlaceholder()) {
				S3Object detailObj = as3.getObject(s3o.getName());
				result.add(new S3PropFile(detailObj));
			}
		}

		return result;
	}

	public S3PropFile read(String filePath) throws ServiceException, IOException {
		final S3Object s3o = as3.getObject(filePath);
		return new S3PropFile(s3o);
	}

	public void save(String filePath, JsonObject meta, InputStream input, String contentType) throws IOException {
		try {
			S3Object s3o = new S3Object(filePath);
			String metaString = (meta == null) ? "{}" : meta.toString();
			byte[] metaByte = metaString.getBytes(Charset.forName("UTF-8"));
			final ConcatInputStream concatInputStream = new ConcatInputStream(new InputStream[] { intToByteArrayStream(metaByte.length), new ByteArrayInputStream(metaByte), input });
			s3o.setDataInputStream(concatInputStream);
			// if (fitem.getSize() > 0 ) s3o.setContentLength(fitem.getSize() + 4 + metaByte.length) ;
			if (StringUtil.isNotBlank(contentType))
				s3o.setContentType(contentType);
			// String metaString = (meta == null) ? "{}" : meta.toString();
			// s3o.addMetadata("meta", metaString) ;
			as3.saveObject(s3o);
		} catch (S3ServiceException ex) {
			throw new IOException(ex);
		} finally {
			IOUtil.closeQuietly(input);
		}
	}

	public void save(String filePath, JsonObject meta, FileItem fitem) throws IOException {
		save(filePath, meta, fitem.getInputStream(), fitem.getContentType());
	}

	public void remove(String filePath) throws S3ServiceException {
		as3.deleteObject(filePath);
	}

	private static final ByteArrayInputStream intToByteArrayStream(int value) {
		return new ByteArrayInputStream(new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value });
	}

}
