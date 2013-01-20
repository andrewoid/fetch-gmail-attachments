package com.andrewoid.imap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.io.IOUtils;

public class Inbox {

	private static final String	MULTIPART_CONTENT_TYPE	= "multipart/";
	private final Store			store;

	public Inbox(final String server, final String email, final String password) throws MessagingException {
		final Properties props = new Properties();
		final Session session = Session.getDefaultInstance(props, null);
		store = session.getStore("imaps");
		store.connect(server, email, password);
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

		for (int i = 0; i < multipart.getCount(); i++) {
			final BodyPart bodyPart = multipart.getBodyPart(i);
			if (isNotAttachment(bodyPart)) {
				continue;
			}
			saveFile(toDir, bodyPart);
		}
	}

	private void saveFile(final File toDir, final BodyPart bodyPart) throws IOException, MessagingException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = bodyPart.getInputStream();
			final File f = new File(toDir, bodyPart.getFileName());
			System.out.println(f);
			if (f.exists()) {
				f.delete();
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
