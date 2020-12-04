package rssreadergui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.net.URL;

import javax.swing.ImageIcon;

public class TrayClass {

	private TrayIcon icon;

	
	public TrayClass() {
		show();
	}
	

	
	

	public void show() {
		
		if (SystemTray.isSupported()) {
			icon = new TrayIcon(createIcon("/home/samuel/eclipse-workspace/TP03XML/src/java.png","/home/samuel/eclipse-workspace/TP03XML/src/javaicon.png"));
			final SystemTray tray = SystemTray.getSystemTray();
			try {
				tray.add(icon);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else {
			System.out.println("Nicht unterstützt");
		}
	}
	
	public Image createIcon(String path, String dest) {
		URL imageUrl = URL.class.getResource(path);
		return (new ImageIcon(imageUrl, dest).getImage());
	}
}
