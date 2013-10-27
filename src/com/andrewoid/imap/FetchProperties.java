package com.andrewoid.imap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

public class FetchProperties extends Properties {

	private static final String	OUTPUT_FILENAME		= "fetch.properties";
	private static final long	serialVersionUID	= 1L;

	public void load() {
		try {
			this.load(new FileInputStream(new File(OUTPUT_FILENAME)));
		}
		catch (final IOException e) {
			// normal if the file doesn't exist
			e.printStackTrace();
		}
	}

	public void save() throws FileNotFoundException {
		final PrintStream printStream = new PrintStream(new FileOutputStream(new File(OUTPUT_FILENAME)));
		this.list(printStream);
		printStream.flush();
		printStream.close();
	}

	public void setServerName(final String serverName) {
		this.setProperty("serverName", serverName);
	}

	public String getServerName() {
		return this.getProperty("serverName", "imap.gmail.com");
	}

	public String getEmailAddress() {
		return this.getProperty("email");
	}

	public void setEmailAddress(final String email) {
		this.setProperty("email", email);
	}

	public void setDownloadLocation(final String downloadLocation) {
		this.setProperty("downloadLocation", downloadLocation);
	}

	public File getDownloadLocation() {
		return new File(getDownloadLocationString());
	}

	public String getDownloadLocationString() {
		return this.getProperty("downloadLocation", "./inbox/");
	}

	public void setGroupByEmailAddress(final boolean groupByEmailAddress) {
		this.setProperty("groupByEmailAddress", String.valueOf(groupByEmailAddress));
	}

	public boolean getGroupByEmailAddress() {
		return Boolean.parseBoolean(this.getProperty("groupByEmailAddress", "true"));
	}

	public void setExtractZipFiles(final boolean extractZipFiles) {
		this.setProperty("extractZipFiles", String.valueOf(extractZipFiles));
	}

	public boolean getExtractZipFiles() {
		return Boolean.parseBoolean(this.getProperty("extractZipFiles", "true"));
	}

	public void setFlattenZipFiles(final boolean flattenZipFiles) {
		this.setProperty("flattenZipFiles", String.valueOf(flattenZipFiles));
	}

	public boolean getFlattenZipFiles() {
		return Boolean.parseBoolean(this.getProperty("flattenZipFiles", "true"));
	}

}
