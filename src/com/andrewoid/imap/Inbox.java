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

// TODO: move the attachment handling to another class
public class Inbox {

	private static final String			MULTIPART_CONTENT_TYPE	= "multipart/";
	private final FetchProperties		properties;
	private final InboxEventListener	listener;

	public Inbox(final FetchProperties properties) throws MessagingException {
		this(properties, new InboxEventListener() {

			@Override
			public void onFileDownloading(final String filename) {
				System.out.println("Downloading " + filename);
			}

			@Override
			public void onFileExtracting(final String filename) {
				System.out.println("Extracting " + filename);
			}

			@Override
			public void onStart() {
				System.out.println("Start");
			}

			@Override
			public void onEnd() {
				System.out.println("End");
			}

		});
	}

	public Inbox(final FetchProperties properties, final InboxEventListener listener) throws MessagingException {
		this.properties = properties;
		this.listener = listener;
	}

	public void downloadAttachments(final String password) throws MessagingException, IOException {
		final Properties props = new Properties();
		final Session session = Session.getDefaultInstance(props, null);
		final Store store = session.getStore("imaps");
		store.connect(properties.getServerName(), properties.getEmailAddress(), password);

		final Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);
		final Message messages[] = inbox.getMessages();

		final File toDir = properties.getDownloadLocation();
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
		final File subDir = properties.getGroupByEmailAddress() ? new File(toDir, emailAddress) : toDir;

		for (int i = 0; i < multipart.getCount(); i++) {
			final BodyPart bodyPart = multipart.getBodyPart(i);
			if (isNotAttachment(bodyPart)) {
				continue;
			}		
			subDir.mkdirs();
			saveFile(subDir, bodyPart);
			final String fileName = bodyPart.getFileName();
			if (properties.getExtractZipFiles() && isZipFile(fileName)) {
				uncompress(subDir, new File(subDir, fileName));
			}
		}
	}

	private void uncompress(final File root, final File f) throws FileNotFoundException, IOException {
		listener.onFileExtracting(f.toString());
		final ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			final String fileName = ze.getName();
			File newFile = new File(root, fileName);
			if (!ze.isDirectory()) {
				if (properties.getFlattenZipFiles()) {
					newFile = new File(root, newFile.getName());
				}
				extractFile(zis, newFile);
				newFile.setLastModified(ze.getTime());
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
		InputStream fileIn = null;
		OutputStream fileOut = null;
		try {
			fileIn = bodyPart.getInputStream();
			final File f = new File(toDir, bodyPart.getFileName());
			listener.onFileDownloading(f.toString());
			fileOut = new FileOutputStream(f);
			IOUtils.copy(fileIn, fileOut);
		}
		finally {
			IOUtils.closeQuietly(fileIn);
			IOUtils.closeQuietly(fileOut);
		}
	}

	private boolean isNotAttachment(final BodyPart bodyPart) throws MessagingException {
		return !Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition());
	}

	private boolean isMultipartMessage(final Message message) throws MessagingException {
		return message.getContentType().startsWith(MULTIPART_CONTENT_TYPE);
	}

}
