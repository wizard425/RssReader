package rssreadergui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

public class UrlInput extends JDialog{

	JTextField input;
	URL url;
	JButton ok;
	
	public UrlInput(JFrame owner) {
		super(owner);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setModal(true);
		setResizable(false);
		setSize(300,150);
		setTitle("Add Url");
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		input = new JTextField();
		input.setBounds(50,30,200,30);
		getContentPane().add(input);
		
		ok = new JButton("Add");
		ok.setBounds(100,70,100,30);
		getContentPane().add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					url = new URL(input.getText());
					dispose();
				} catch (MalformedURLException e) {
					input.setText("Bitte geben Sie eine URL ein");
				}
			}
		});
	}
	
	public URL getText() {

		return url;
	}
}
