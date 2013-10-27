package com.andrewoid.imap;

import java.io.IOException;
import java.util.Scanner;

import javax.mail.MessagingException;

public class FetchMain {

	public static void main(final String args[]) throws MessagingException, IOException {
		final Scanner scanner = new Scanner(System.in);
		System.out.println("Enter server: ");
		final String server = scanner.nextLine();
		System.out.println("Enter email: ");
		final String email = scanner.nextLine();
		System.out.println("Enter password: ");
		final String password = scanner.nextLine();
		scanner.close();
		final FetchProperties properties = new FetchProperties();
		properties.setDownloadLocation("./tmp");
		properties.setEmailAddress(email);
		properties.setServerName(server);
		final Inbox inbox = new Inbox(properties);
		inbox.downloadAttachments(password);
	}

}
