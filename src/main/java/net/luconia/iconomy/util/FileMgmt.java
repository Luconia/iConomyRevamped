package net.luconia.iconomy.util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileMgmt {

	private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static final Lock writeLock = readWriteLock.writeLock();

	/**
	 * Checks a filePath to see if it exists, if it doesn't it will attempt to
	 * create the file at the designated path.
	 *
	 * @param filePath {@link String} containing a path to a file.
	 * @return True if the folder exists or if it was successfully created.
	 */
	public static boolean checkOrCreateFile(String filePath) {
		File file = new File(filePath);
		if (!checkOrCreateFolder(file.getParentFile().getPath())) {
			return false;
		}

		if (file.exists()) {
			return true;
		}

		return newFile(file);
	}

	/**
	 * Checks a folderPath to see if it exists, if it doesn't it will attempt to
	 * create the folder at the designated path.
	 *
	 * @param folderPath {@link String} containing a path to a folder.
	 * @return True if the folder exists or if it was successfully created.
	 */
	private static boolean checkOrCreateFolder(String folderPath) {
		File file = new File(folderPath);

		if (file.exists() || file.isDirectory()) {
			return true;
		}

		return newDir(file);
	}

	private static boolean newDir(File dir) {
		try {
			writeLock.lock();
			return dir.mkdirs();
		} finally {
			writeLock.unlock();
		}
	}

	private static boolean newFile(File file) {
		try {
			writeLock.lock();
			return file.createNewFile();
		} catch (IOException e) {
			return false;
		} finally {
			writeLock.unlock();
		}
	}

}
