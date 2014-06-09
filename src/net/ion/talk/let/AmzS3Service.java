package net.ion.talk.let;

import net.ion.radon.core.IService;
import net.ion.radon.core.context.OnEventObject;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class AmzS3Service implements OnEventObject {

	private S3Service s3;
	private S3Bucket bucket;

	private final String location;

	public AmzS3Service(String key, String pwd) throws S3ServiceException {
		this(key, pwd, S3Bucket.LOCATION_ASIA_PACIFIC_TOKYO);
	}

	public AmzS3Service(String key, String pwd, String location) throws S3ServiceException {
		this.s3 = new RestS3Service(new AWSCredentials(key, pwd));
		this.location = location;
		this.bucket = s3.getBucket("toonfile");
	}

	static AmzS3Service test() throws S3ServiceException {
		return new AmzS3Service("", "", S3Bucket.LOCATION_ASIA_PACIFIC_TOKYO);
	}

	public void destroySelf() throws ServiceException {
		s3.shutdown();
	}

	@Override
	public void onEvent(AradonEvent event, IService service) {
		try {
			if (event == AradonEvent.START) {
				createBucket();
			} else {
				destroy();
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public void initBucket(String bucketName) throws ServiceException, S3ServiceException {
		if (!s3.isBucketAccessible(bucketName)) {
			throw new ServiceException(bucketName + " not exist or not accessible");
		}
		this.bucket = s3.getBucket(bucketName);
	}

	private void createBucket() throws ServiceException, S3ServiceException {
		final String bucketName = "toonfile";
		if (!s3.isBucketAccessible(bucketName)) {
			S3Bucket bucket = s3.createBucket(bucketName, location);
		}
		this.bucket = s3.getBucket(bucketName);
	}

	private void destroy() throws ServiceException {
		s3.shutdown();
	}

	public void saveObject(S3Object s3o) throws S3ServiceException {
		s3.putObject(bucket, s3o);
	}

	public S3Service getService() {
		return s3;
	}

	public void deleteObject(String filePath) throws S3ServiceException {
		s3.deleteObject(bucket, filePath);
	}

	public S3Object getObject(String filePath) throws S3ServiceException {
		return s3.getObject(bucket, filePath);
	}

	public S3Object[] listBy(String prefixResourcePath) throws S3ServiceException {
		S3Object[] s3os = s3.listObjects(bucket, prefixResourcePath, null);
		return s3os;
	}

}

// <configured-object id="net.ion.toon.aradon.upload.AmzS3Service" scope="application">
// <class-name>net.ion.toon.aradon.upload.AmzS3Service</class-name>
// <constructor>
// <constructor-param>
// <description>sec key</description>
// <type>java.lang.String</type>
// <value>AKIAIVKXQXFRBCJJRLPQ</value>
// </constructor-param>
// <constructor-param>
// <description>sec pwd</description>
// <type>java.lang.String</type>
// <value>Fof2dm0CzKiZ4alyjiyvcABMUUGhEFhROwwOyG9S</value>
// </constructor-param>
// <constructor-param>
// <description>location</description>
// <type>java.lang.String</type>
// <value>ap-northeast-1</value>
// </constructor-param>
// <constructor-param>
// <description>bucketname</description>
// <type>java.lang.String</type>
// <value>toonfilebytest</value>
// </constructor-param>
// </constructor>
// </configured-object>
