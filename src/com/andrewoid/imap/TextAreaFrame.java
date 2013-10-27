package com.andrewoid.imap;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextAreaFrame extends JFrame {

	private static final long	serialVersionUID	= 1L;
	private final JTextArea		textArea;

	public TextAreaFrame() {
		this.setSize(800, 600);
		this.setTitle("Inbox output");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		textArea = new JTextArea();

		contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);

		this.setVisible(true);
	}

	public void append(final String s) {
		textArea.append(s);
	}

	public JTextArea getTextArea() {
		return textArea;
	}

}
