package net.ion.talk.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ConcatInputStream extends InputStream {

	private int inputStreamQueueIndex = 0;
	private ArrayList<InputStream> inputStreamQueue = new ArrayList<InputStream>();

	private InputStream currentInputStream = null;
	private boolean doneAddingInputStreams = false;

	public void lastInputStreamAdded() {
		doneAddingInputStreams = true;
	}

	public void addInputStream(InputStream in) {
		synchronized (inputStreamQueue) {
			if (in == null)
				throw new NullPointerException();
			if (closed)
				throw new IllegalStateException("ConcatInputStream has been closed");
			if (doneAddingInputStreams)
				throw new IllegalStateException("Cannot add more inputStreams - the last inputStream has already been added.");
			inputStreamQueue.add(in);
		}
	}

	public void addInputStreams(InputStream[] in) {
		for (InputStream element : in) {
			addInputStream(element);
		}
	}

	private InputStream getCurrentInputStream() {
		if (currentInputStream == null && inputStreamQueueIndex < inputStreamQueue.size()) {
			synchronized (inputStreamQueue) {
				// inputStream queue index is advanced only by the nextInputStream()
				// method. Don't do it here.
				currentInputStream = inputStreamQueue.get(inputStreamQueueIndex);
			}
		}
		return currentInputStream;
	}

	private void advanceToNextInputStream() {
		currentInputStream = null;
		inputStreamQueueIndex++;
	}

	private boolean closed = false;

	public ConcatInputStream() {
		// Empty constructor
	}

	public ConcatInputStream(InputStream in) {
		addInputStream(in);
		lastInputStreamAdded();
	}

	public ConcatInputStream(InputStream in1, InputStream in2) {
		addInputStream(in1);
		addInputStream(in2);
		lastInputStreamAdded();
	}

	public ConcatInputStream(InputStream[] in) {
		addInputStreams(in);
		lastInputStreamAdded();
	}

	@Override
	public int read() throws IOException {
		if (closed)
			throw new IOException("InputStream closed");
		int r = -1;
		while (r == -1) {
			InputStream in = getCurrentInputStream();
			if (in == null) {
				if (doneAddingInputStreams)
					return -1;
				try {
					Thread.sleep(100);
				} catch (InterruptedException iox) {
					throw new IOException("Interrupted");
				}
			} else {
				r = in.read();
				if (r == -1)
					advanceToNextInputStream();
			}
		}
		return r;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (off < 0 || len < 0 || off + len > b.length)
			throw new IllegalArgumentException();
		if (closed)
			throw new IOException("InputStream closed");
		int r = -1;
		while (r == -1) {
			InputStream in = getCurrentInputStream();
			if (in == null) {
				if (doneAddingInputStreams)
					return -1;
				try {
					Thread.sleep(100);
				} catch (InterruptedException iox) {
					throw new IOException("Interrupted");
				}
			} else {
				r = in.read(b, off, len);
				if (r == -1)
					advanceToNextInputStream();
			}
		}
		return r;
	}

	@Override
	public long skip(long n) throws IOException {
		if (closed)
			throw new IOException("InputStream closed");
		if (n <= 0)
			return 0;
		long s = -1;
		while (s <= 0) {
			InputStream in = getCurrentInputStream();
			if (in == null) {
				if (doneAddingInputStreams)
					return 0;
				try {
					Thread.sleep(100);
				} catch (InterruptedException iox) {
					throw new IOException("Interrupted");
				}
			} else {
				s = in.skip(n);
				// When nothing was skipped it is a bit of a puzzle.
				// The most common cause is that the end of the underlying
				// stream was reached. In which case calling skip on it
				// will always return zero. If somebody were calling skip
				// until it skipped everything they needed, there would
				// be an infinite loop if we were to return zero here.
				// If we get zero, let us try to read one character so
				// we can see if we are at the end of the stream. If so,
				// we will move to the next.
				if (s <= 0) {
					// read() will advance to the next stream for us, so don't do it again
					s = ((read() == -1) ? -1 : 1);
				}
			}

		}
		return s;
	}

	@Override
	public int available() throws IOException {
		if (closed)
			throw new IOException("InputStream closed");
		InputStream in = getCurrentInputStream();
		if (in == null)
			return 0;
		return in.available();
	}

	@Override
	public void close() throws IOException {
		if (closed)
			return;
		for (Object element : inputStreamQueue) {
			((InputStream) element).close();
		}
		closed = true;
	}

	@Override
	public void mark(int readlimit) {
		// Mark not supported -- do nothing
	}

	@Override
	public void reset() throws IOException {
		throw new IOException("Reset not supported");
	}

	@Override
	public boolean markSupported() {
		return false;
	}
}
