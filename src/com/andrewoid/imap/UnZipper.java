package com.andrewoid.imap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class UnZipper {

	private File dir;

	public UnZipper(File dir) {
		this.dir = dir;
	}

	public void unzipAllFiles() throws IOException {
		for (File f : dir.listFiles()) {
			if (isZipFile(f)) {
				extractAllFiles(f);
			}
		}
	}

	private void extractAllFiles(File f) throws FileNotFoundException,
			IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String fileName = ze.getName();
			File newFile = new File(dir, fileName);
			if (ze.isDirectory()) {
				newFile.mkdirs();
			} else {
				extractFile(zis, newFile);
			}
		}

		zis.closeEntry();
		zis.close();
	}

	private void extractFile(ZipInputStream zis, File newFile)
			throws FileNotFoundException, IOException {
		new File(newFile.getParent()).mkdirs();

		FileOutputStream fos = new FileOutputStream(newFile);

		IOUtils.copy(zis, fos);

		fos.close();
	}

	private boolean isZipFile(File f) {
		return f.getName().endsWith(".zip");
	}

}
