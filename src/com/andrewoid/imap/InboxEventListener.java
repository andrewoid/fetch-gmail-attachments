package com.andrewoid.imap;

public interface InboxEventListener {

	public void onStart();

	public void onFileDownloading(String filename);

	public void onFileExtracting(String filename);

	public void onEnd();
}
