package com.andrewoid.imap;

public interface InboxEventListener {
	public void onFileDownloading(String filename);

	public void onFileExtracting(String filename);
}
