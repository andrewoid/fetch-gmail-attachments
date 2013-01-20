package com.andrewoid.imap;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GuiFetchMain extends JFrame implements ActionListener {

	private static final long		serialVersionUID	= 1L;

	private final JTextField		serverField;
	private final JTextField		emailField;
	private final JPasswordField	passwordField;
	private final JButton			submit;

	public GuiFetchMain() {

		this.setTitle("Fetch Inbox Mail Attachments");
		this.setSize(250, 175);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(4, 2));

		contentPane.add(new JLabel("Server: "));
		serverField = new JTextField("imap.gmail.com");
		contentPane.add(serverField);

		contentPane.add(new JLabel("Email: "));
		emailField = new JTextField();
		contentPane.add(emailField);

		contentPane.add(new JLabel("Password: "));
		passwordField = new JPasswordField();
		contentPane.add(passwordField);

		contentPane.add(new JLabel());
		submit = new JButton("Submit");
		contentPane.add(submit);

		submit.addActionListener(this);

	}

	public static void main(final String args[]) {
		new GuiFetchMain().setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		try {
			final Inbox inbox = new Inbox(serverField.getText(), emailField.getText(), passwordField.getText());
			final File dir = new File("./inbox");
			inbox.downloadAttachments(dir);
			final UnZipper unzipper = new UnZipper(dir);
			unzipper.unzipAllFiles();
		}
		catch (final MessagingException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
