package rssreadergui;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.*;

public class Gui extends JFrame {

	private JButton addUrl, update, disactivate;
	private JEditorPane pane;
	private JScrollPane scroll;

	private JLabel urlanzahl;

	private UrlInput u;

	private ArrayList<URL> urls = new ArrayList<URL>();
	private ArrayList<Item> latestitem = new ArrayList<Item>();

	private ScheduledExecutorService s;

	private boolean schedulerActivated = false;

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Gui g = new Gui();
				g.setVisible(true);
			}
		});

	}

	public Gui() {
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setLocationRelativeTo(null);
		setTitle("Simple Rss Reader");
		setResizable(false);
		
		TrayClass t = new TrayClass();

		Container con = getContentPane();

		pane = new JEditorPane("text/html", "");
		pane.setEditable(false);

		scroll = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(0, 0, 1000, 500);
		con.add(scroll);

		urlanzahl = new JLabel("Aktuelle Url's: " + urls.size());
		urlanzahl.setBounds(20, 510, 200, 40);
		con.add(urlanzahl);

		ExecutorService executor = Executors.newCachedThreadPool();

		addUrl = new JButton("Add URL...");
		addUrl.setBounds(500, 510, 120, 40);
		con.add(addUrl);
		addUrl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				u = new UrlInput(Gui.this);
				u.setVisible(true);
				urls.add(u.getText());
				urlanzahl.setText("Aktuelle Url's: " + urls.size());
				String out = pane.getText().replaceAll("</*html>|</*head>|</*body>|\n|\r", "");
				out = out.trim();
				pane.setText("<b>Message:</b> New URL added: " + urls.get(urls.size() - 1).toString() + "<br>" + out);
				// Runnable r = new RssReader(urls.get(urls.size() - 1), pane);
				// executor.execute(r);
				if (schedulerActivated) {
					startScheduler();
				}
			}
		});

		disactivate = new JButton("Activate Scheduler");
		disactivate.setBounds(630, 510, 250, 40);
		con.add(disactivate);
		disactivate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (schedulerActivated) {
					String out = pane.getText().replaceAll("</*html>|</*head>|</*body>|\n|\r", "");
					out = out.trim();
					pane.setText("<b>Message:</b> Scheduler is stopped<br>" + out);
					schedulerActivated = false;
					startScheduler();
					disactivate.setText("Activate Scheduler");
				} else {
					String out = pane.getText().replaceAll("</*html>|</*head>|</*body>|\n|\r", "");
					out = out.trim();
					pane.setText("<b>Message:</b>Scheduler is now running:(schedule period = 10 sec)<br>" + out);

					schedulerActivated = true;
					startScheduler();
					disactivate.setText("Disactivate Scheduler");
				}
			}
		});

		update = new JButton("Update");
		update.setBounds(890, 510, 100, 40);
		con.add(update);
		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String out = pane.getText().replaceAll("</*html>|</*head>|</*body>|\n|\r", "");
				out = out.trim();
				pane.setText("<b>Message:</b> Updating channels...<br>" + out);
				for (int i = 0; i < urls.size(); i++) {
					RssReader r = new RssReader(urls.get(i), pane);
					executor.execute(r);
				}

			}
		});

	}

	public void startScheduler() {
		if (schedulerActivated) {
			if (s != null) {
				s.shutdown();
			}
			s = Executors.newScheduledThreadPool(urls.size());
			for (int i = 0; i < urls.size(); i++) {
				RssReader r = new RssReader(urls.get(i), pane);
				s.scheduleAtFixedRate(r, 10, 10, TimeUnit.SECONDS);
			}

		} else {
			s.shutdown();
		}
	}

}
