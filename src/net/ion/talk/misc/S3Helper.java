package net.ion.talk.misc;

import java.io.ByteArrayInputStream;
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

public class S3Helper {

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
