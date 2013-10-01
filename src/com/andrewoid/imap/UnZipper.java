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

	private final File	root;

	public UnZipper(final File dir) {
		this.root = dir;
	}

	public void unzipAllFiles() throws IOException {
		unzipAllFiles(root);
	}

	public void unzipAllFiles(final File dir) throws IOException {
		for (final File f : dir.listFiles()) {
			if (f.isDirectory()) {
				unzipAllFiles(f);
			}
			else if (isZipFile(f)) {
				extractAllFiles(dir, f);
			}
		}
	}

	private void extractAllFiles(final File dir, final File f) throws FileNotFoundException, IOException {
		System.out.println("Extracting " + f);
		final ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			final String fileName = ze.getName();
			final File newFile = new File(dir, fileName);
			if (ze.isDirectory()) {
				newFile.mkdirs();
			}
			else {
				extractFile(zis, newFile);
			}
		}

		zis.closeEntry();
		zis.close();
		f.delete();
	}

	private void extractFile(final ZipInputStream zis, final File newFile) throws FileNotFoundException, IOException {
		new File(newFile.getParent()).mkdirs();

		final FileOutputStream fos = new FileOutputStream(newFile);

		IOUtils.copy(zis, fos);

		fos.close();
	}

	private boolean isZipFile(final File f) {
		return f.getName().endsWith(".zip");
	}

}
