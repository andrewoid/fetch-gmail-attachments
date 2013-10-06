package com.andrewoid.imap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;

public class Inbox {

	private static final String	MULTIPART_CONTENT_TYPE	= "multipart/";
	private final Store			store;
	private final boolean		uncompressZipFiles;
	private final boolean		flatten;
	private final boolean		groupByEmailAddress;

	public Inbox(	final String server,
					final String email,
					final String password,
					final boolean uncompressZipFiles,
					final boolean flatten,
					final boolean groupByEmailAddress) throws MessagingException {
		final Properties props = new Properties();
		final Session session = Session.getDefaultInstance(props, null);
		store = session.getStore("imaps");
		store.connect(server, email, password);
		this.uncompressZipFiles = uncompressZipFiles;
		this.flatten = flatten;
		this.groupByEmailAddress = groupByEmailAddress;
	}

	public void downloadAttachments(final File toDir) throws MessagingException, IOException {
		final Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);
		final Message messages[] = inbox.getMessages();

		toDir.mkdirs();

		for (final Message message : messages) {
			if (isMultipartMessage(message)) {
				handleMessage(toDir, message);
			}
		}
	}

	private void handleMessage(final File toDir, final Message message) throws IOException, MessagingException {
		final Multipart multipart = (Multipart) message.getContent();
		final Address[] froms = message.getFrom();
		final String emailAddress = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
		final File subDir = groupByEmailAddress ? new File(toDir, emailAddress) : toDir;
		subDir.mkdirs();

		for (int i = 0; i < multipart.getCount(); i++) {
			final BodyPart bodyPart = multipart.getBodyPart(i);
			if (isNotAttachment(bodyPart)) {
				continue;
			}
			saveFile(subDir, bodyPart);
			final String fileName = bodyPart.getFileName();
			if (uncompressZipFiles && isZipFile(fileName)) {
				uncompress(subDir, new File(subDir, fileName));
			}
		}
	}

	private void uncompress(final File root, final File f) throws FileNotFoundException, IOException {
		System.out.println("Extracting " + f);
		final ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			final String fileName = ze.getName();
			File newFile = new File(root, fileName);
			if (!ze.isDirectory()) {
				if (flatten) {
					newFile = new File(root, newFile.getName());
				}
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

	private boolean isZipFile(final String filename) {
		return filename.endsWith(".zip");
	}

	private void saveFile(final File toDir, final BodyPart bodyPart) throws IOException, MessagingException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = bodyPart.getInputStream();
			final File f = new File(toDir, bodyPart.getFileName());
			System.out.println(f);
			if (f.exists()) {
				return;
				// f.delete();
			}
			out = new FileOutputStream(f);
			IOUtils.copy(in, out);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	private boolean isNotAttachment(final BodyPart bodyPart) throws MessagingException {
		return !Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition());
	}

	private boolean isMultipartMessage(final Message message) throws MessagingException {
		return message.getContentType().startsWith(MULTIPART_CONTENT_TYPE);
	}

}
