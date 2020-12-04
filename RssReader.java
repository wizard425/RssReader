package rssreadergui;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.tfobz.rssreader.Channel;
import net.tfobz.rssreader.Item;

public class RssReader implements Runnable {

	URL url;
	JEditorPane pane;
	String ret = "";

	public enum State {
		ITEM, CHANNEL
	}

	State aktState;

	public RssReader(URL url, JEditorPane pane) {
		this.url = url;
		this.pane = pane;
	}

	@Override
	public void run() {
		read();
	}

	public void read() {

		String ret = "";

		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream input = null;
		try {
			input = url.openStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
		ArrayList<Item> items = new ArrayList<Item>();
		Channel ch = null;
		Item it = null;

		XMLStreamReader xmlreader;
		try {
			xmlreader = factory.createXMLStreamReader(input);

			String characters = "";
			while (xmlreader.hasNext()) {
				int elementType = xmlreader.next();
				switch (elementType) {
				case XMLStreamConstants.END_DOCUMENT: {
					// wenn xml-Dokument fertig hat
					xmlreader.close();
					// System.out.println("Dokument fertig");
					break;
				}

				case XMLStreamConstants.START_ELEMENT: {
					// System.out.println(elementType + "START_DOCUMENT: " +
					// xmlreader.getLocalName());
					// wenn ein neuer Channel beginnt
					if (xmlreader.getLocalName().equals("channel")) {
						ch = new Channel();
						//System.out.println("Channel erstellt");
						ch.setUrl(url.getPath());
						aktState = State.CHANNEL;
						// wenn der Channel schon besteht und man noch Items hinzufügen will
					} else if (xmlreader.getLocalName().equals("item")) {
						it = new Item();
						aktState = State.ITEM;
					}

					break;
				}

				case XMLStreamConstants.START_DOCUMENT: {
					// System.out.println(elementType + "START_DOCUMENT: " +
					// xmlreader.getLocalName());
					// wenn ein neuer Channel beginnt
					if (xmlreader.getLocalName().equals("channel")) {
						ch = new Channel();
						ch.setUrl(url.getPath());
						aktState = State.CHANNEL;
						// wenn der Channel schon besteht und man noch Items hinzufügen will
					} else if (xmlreader.getLocalName().equals("item")) {
						it = new Item();
						aktState = State.ITEM;
					}

					break;
				}

				case XMLStreamConstants.END_ELEMENT: {
					// System.out.println(elementType + " END_ELEMENT: " +
					// xmlreader.getLocalName());
					if (xmlreader.getLocalName().equals("item")) {
						items.add(it);
						aktState = State.CHANNEL;
						it = null;
					} else if (xmlreader.getLocalName().equals("channel")) {
						aktState = null;
					}
					if (it != null && aktState == State.ITEM) {
						switch (xmlreader.getLocalName()) {

						case ("title"): {
							it.setTitle(characters);
							break;
						}
						case ("link"): {
							it.setLink(characters);
							break;
						}

						case ("pubDate"): {
							it.setPubDate(characters);
							break;
						}

						case ("description"): {
							it.setDescription(characters);
							break;
						}
						case ("author"): {
							it.setAuthor(characters);
							break;
						}

						}
					}
					if (ch != null && aktState == State.CHANNEL) {
						switch (xmlreader.getLocalName()) {
						case ("title"): {
							ch.setTitle(characters);
							break;
						}
						case ("link"): {
							ch.setLink(characters);
							break;
						}
						case ("language"): {
							ch.setLanguage(characters);
							break;
						}
						case ("despcription"): {
							ch.setDescription(characters);
							break;
						}
						case ("copyright"): {
							ch.setCopyright(characters);
							break;
						}
						}

					}

					characters = "";
					break;
				}
				case XMLStreamConstants.CHARACTERS: {
					if (!xmlreader.isWhiteSpace() && xmlreader.getText() != null && xmlreader.getText().length() > 0)
						characters += xmlreader.getText();
					// System.out.println(characters);
					break;
				}

				}
			}

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String out = pane.getText().replaceAll("</*html>|</*head>|</*body>|\n|\r", "");
		out = out.trim();
		if (out.length() > 39) {
			ret =   "<b>Channel:</b> " + ch.getTitle() + " <b>Newest Item:</b> " + items.get(0).getTitle()
					+ " <b>Date:</b> " + items.get(0).getPubDate() + "<br>" + out;
		}else {
			ret =  "<b>Channel:</b> " + ch.getTitle() + " <b>Newest Item:</b> " + items.get(0).getTitle()
					+ " <b>Date:</b> " + items.get(0).getPubDate() + "<br>";
		}

		pane.setText(ret);
	}
}
