package com.gundersoft.skope3;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Builds a tray icon for Skope with parameters read from the
 * Configuration.
 * @author Christian Gunderman
 */
public class TrayIconBuilder {
	
    /**
     * Builds a tray icon from the parameters contained in the 
     * current instance's Configuration object.
     * @param instance An instance of Skope.
     * @return The TrayIcon object that was created and added to 
     * the tray.
     */
    public static TrayIcon build(final Main instance) {
	// if the system doesn't support system trays
	if(!SystemTray.isSupported())
	    return null;
		
	// get the image icon for the tray
	Image icon = null;
	icon = instance.getBuilder().getIconImage();
		
	// build TrayIcon
	final TrayIcon trayIcon = new TrayIcon(icon);
		
	// create pop up menu for tray icon
	final PopupMenu popupMenu = new PopupMenu();
	popupMenu.add("About");
	popupMenu.add("Activate/Deactivate");
	popupMenu.add("Settings");
	popupMenu.add("Exit");
	popupMenu.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
		    if(event.getActionCommand().equals("About")) {
			popupMenu.setEnabled(false);
			AboutDialog.displayAboutBox(null, instance);
			popupMenu.setEnabled(true);
		    } else if(event.getActionCommand().equals("Exit")) {
			popupMenu.setEnabled(false);
			if(instance.getSecurity().auditPasswordDialog(null, instance.getBuilder()))
			    instance.terminate();
		    } else if(event.getActionCommand().equals("Activate/Deactivate")) {
			popupMenu.setEnabled(false);
			if(instance.getSecurity().auditPasswordDialog(null, instance.getBuilder())) {
			    if(instance.getService().isEnabled()) {
				instance.stopService();
			    } else {
				instance.startService();
			    }
			}
			popupMenu.setEnabled(true);
		    } else if (event.getActionCommand().equals("Settings")) {
			popupMenu.setEnabled(false);
			instance.launchUI();
			popupMenu.setEnabled(true);
		    }
		}
	    });
	trayIcon.setPopupMenu(popupMenu);
	trayIcon.setImageAutoSize(true);
		
	// add icon to the tray
	try {
	    SystemTray.getSystemTray().add(trayIcon);
	} catch (AWTException e) {
	    return null;
	}
	return trayIcon;
    }
}
