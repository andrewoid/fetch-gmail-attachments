package com.andrewoid.imap;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
	private final FetchProperties	properties;

	private final JButton			directoryField;

	private final JCheckBox			extractZips;

	private final JCheckBox			flattenZips;

	private final JCheckBox			groupFiles;

	public GuiFetchMain() {

		properties = new FetchProperties();
		properties.load();

		this.setTitle("Fetch Inbox Mail Attachments");
		this.setSize(400, 350);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(8, 2));

		contentPane.add(new JLabel("Server:"));
		serverField = new JTextField(properties.getServerName());
		contentPane.add(serverField);

		contentPane.add(new JLabel("Email:"));
		emailField = new JTextField(properties.getEmailAddress());
		contentPane.add(emailField);

		contentPane.add(new JLabel("Password:"));
		passwordField = new JPasswordField();
		contentPane.add(passwordField);

		contentPane.add(new JLabel("Download Directory:"));
		directoryField = new JButton(properties.getDownloadLocationString());
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directoryField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final int returnVal = fileChooser.showOpenDialog(GuiFetchMain.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File file = fileChooser.getSelectedFile();
					final String absolutePath = file.getAbsolutePath();
					directoryField.setText(absolutePath);
				}
			}

		});
		contentPane.add(directoryField);

		contentPane.add(new JLabel("Group Files:"));
		groupFiles = new JCheckBox();
		groupFiles.setSelected(properties.getGroupByEmailAddress());
		contentPane.add(groupFiles);

		contentPane.add(new JLabel("Decompress Zips:"));
		extractZips = new JCheckBox();
		extractZips.setSelected(properties.getExtractZipFiles());
		contentPane.add(extractZips);

		contentPane.add(new JLabel("Flatten Zips:"));
		flattenZips = new JCheckBox();
		flattenZips.setSelected(properties.getFlattenZipFiles());
		contentPane.add(flattenZips);

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
		saveProperties();
		// submit.setText("Downloading...");
		// submit.setEnabled(false);
		final String password = new String(passwordField.getPassword());

		final TextAreaFrame textAreaFrame = new TextAreaFrame();
		textAreaFrame.setVisible(true);

		// TODO: probably move this into TextAreaFrame?
		final InboxWorker worker = new InboxWorker(properties, password, textAreaFrame.getTextArea());
		worker.execute();
	}

	private void saveProperties() {
		properties.setEmailAddress(emailField.getText());
		properties.setServerName(serverField.getText());
		properties.setDownloadLocation(directoryField.getText());
		properties.setExtractZipFiles(extractZips.isSelected());
		properties.setFlattenZipFiles(flattenZips.isSelected());
		properties.setGroupByEmailAddress(groupFiles.isSelected());
		try {
			properties.store();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void resetSubmitButton() {
		submit.setText("Submit");
		submit.setEnabled(true);
	}
}
