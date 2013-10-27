package com.andrewoid.imap;

import java.util.List;

import javax.swing.SwingWorker;

public class InboxWorker extends SwingWorker<Void, String> implements InboxEventListener {

	private final FetchProperties	properties;
	private final String			password;

	// TODO: what are the security implications of passing around a String
	// password?
	public InboxWorker(final FetchProperties properties, final String password) {
		super();
		this.properties = properties;
		this.password = password;
	}

	@Override
	public void onFileDownloading(final String filename) {
		publish(filename);
	}

	@Override
	public void onFileExtracting(final String filename) {
		publish(filename);
	}

	@Override
	protected Void doInBackground() throws Exception {
		final Inbox inbox = new Inbox(properties, this);
		inbox.downloadAttachments(password);
		return null;
	}

	@Override
	protected void done() {
		super.done();
	}

	@Override
	protected void process(final List<String> chunks) {
		for (final String s : chunks) {
			System.out.println(s);
		}
	}

}
