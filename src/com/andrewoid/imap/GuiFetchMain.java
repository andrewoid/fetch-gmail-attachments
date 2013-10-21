package com.andrewoid.imap;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

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
		directoryField = new JButton(properties.getDownloadLocation());
		final JFileChooser fileChooser = new JFileChooser();
		directoryField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final int returnVal = fileChooser.showOpenDialog(GuiFetchMain.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File file = fileChooser.getSelectedFile();
					directoryField.setText(file.getAbsolutePath());
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
		final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				properties.setEmailAddress(emailField.getText());
				properties.setServerName(serverField.getText());
				properties.setDownloadLocation(directoryField.getText());
				properties.setExtractZipFiles(extractZips.isSelected());
				properties.setFlattenZipFiles(flattenZips.isSelected());
				properties.setGroupByEmailAddress(groupFiles.isSelected());
				properties.save();
				final Inbox inbox = new Inbox(serverField.getText(), emailField.getText(), passwordField.getText(),
						extractZips.isSelected(), flattenZips.isSelected(), groupFiles.isSelected());
				final File dir = new File(directoryField.getText());
				submit.setText("Downloading...");
				submit.setEnabled(false);
				inbox.downloadAttachments(dir);
				return null;
			}

			@Override
			protected void done() {
				super.done();

				submit.setText("Submit");
				submit.setEnabled(true);
			}
		};
		worker.execute();
	}
}
