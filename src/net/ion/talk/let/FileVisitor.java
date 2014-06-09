package net.ion.talk.let;

import java.io.File;

public class FileVisitor {

	public interface Visitor<T> {
		public void handle(File file);

		public T result();
	}

	public static <T> T walkFile(File source, Visitor<T> visitor) {
		if (!source.exists())
			return visitor.result();

		if (source.isDirectory()) {
			for (File child : source.listFiles()) {
				walkFile(child, visitor);
			}
		} else {
			visitor.handle(source);
		}
		return visitor.result();
	}
}
