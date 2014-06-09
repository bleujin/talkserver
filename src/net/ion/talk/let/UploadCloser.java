package net.ion.talk.let;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.ion.framework.schedule.IExecutor;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.radon.core.IService;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.context.OnEventObject;

public abstract class UploadCloser implements OnEventObject {

	private long limitSizeM;
	private int scheduleMinute;
	private File baseDir;

	protected UploadCloser(long limitSizeM, int scheduleMinute) {
		this.limitSizeM = limitSizeM;
		this.scheduleMinute = scheduleMinute;
	}

	public void onEvent(AradonEvent event, IService service) {
		if (event == AradonEvent.START) {
			final TreeContext context = service.getServiceContext();
			IExecutor exec = context.getAttributeObject(IExecutor.class.getCanonicalName(), IExecutor.class);
			run(new File(context.getAttributeObject("upload.base.dir", "./resource/temp", String.class)), exec);
		}
	}

	protected void run(File baseDir, IExecutor exec) {
		this.baseDir = baseDir;
		exec.schedule(new CloserJob(this, exec), scheduleMinute, TimeUnit.MINUTES);
		Debug.warn(this.getClass().getName() + " Started..");
	}

	public abstract void handle(File targetFile);

	long limitSizeM() {
		return limitSizeM;
	}

	long scheduleMinute() {
		return scheduleMinute;
	}

	File baseDir() {
		return baseDir;
	}

}

class CloserJob implements Callable<Long> {

	private UploadCloser closer;
	private IExecutor exec;

	public CloserJob(UploadCloser uploadCloser, IExecutor exec) {
		this.closer = uploadCloser;
		this.exec = exec;
	}

	@Override
	public Long call() throws IOException {
		try {

			FileVisitor.Visitor<StatusInfo> visitor = new FileVisitor.Visitor<StatusInfo>() {
				private long totalCount = 0;
				private long totalSize = 0;
				private File oldestFile = null;
				private File newestFile = null;

				public void handle(File file) {
					totalCount++;
					totalSize += file.length();
					if (oldestFile == null) {
						this.oldestFile = file;
						this.newestFile = file;
					}

					if (FileUtil.isFileOlder(file, this.oldestFile))
						this.oldestFile = file;
					if (FileUtil.isFileNewer(file, this.newestFile))
						this.newestFile = file;
				}

				public StatusInfo result() {
					return new StatusInfo(oldestFile, newestFile, totalSize, totalCount);
				}
			};
			File baseDir = closer.baseDir();
			final StatusInfo sinfo = FileVisitor.walkFile(baseDir, visitor);

			if (sinfo.isOverLimit(closer.limitSizeM())) {
				FileVisitor.Visitor<Long> visitHandler = new FileVisitor.Visitor<Long>() {
					private long stdTime = sinfo.stdTime();
					private long deletedFileSize = 0;

					@Override
					public void handle(File file) {
						if (FileUtil.isFileOlder(file, stdTime)) {

							closer.handle(file);
							deletedFileSize += file.length();
						}
					}

					@Override
					public Long result() {
						return deletedFileSize;
					}
				};

				final Long removedSize = FileVisitor.walkFile(baseDir, visitHandler);
				Debug.line("UploadFile Closer End... " + removedSize + " byte removed");
				return removedSize;
			}

			return 0L;
		} finally {
			exec.schedule(this, closer.scheduleMinute(), TimeUnit.MINUTES);
		}
	}
}

class StatusInfo {
	private File oldestFile;
	private File newestFile;
	private long totalSize;
	private long totalCount;

	public StatusInfo(File oldestFile, File newestFile, long totalSize, long totalCount) {
		this.oldestFile = oldestFile;
		this.newestFile = newestFile;
		this.totalSize = totalSize;
		this.totalCount = totalCount;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public long stdTime() {
		return oldestFile.lastModified() + (newestFile.lastModified() - oldestFile.lastModified()) / 5;
	}

	public boolean isOverLimit(long maxLimitM) {
		return totalSize > maxLimitM * 1024 * 1024;
	}
}
