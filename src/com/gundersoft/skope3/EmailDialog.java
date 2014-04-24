package com.gundersoft.skope3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * Dialog box for email settings.
 * @author Christian Gunderman
 */
public class EmailDialog {
	
    /**
     * Displays the email dialog box
     * @param parent The parent window to this dialog.
     * @param instance The current instance of Skope 3.
     */
    public static void display(final UIBuilder.Window parent, final Main instance) {
		
	// get UI instance
	final UIBuilder builder = instance.getBuilder();
		
	// get config instance
	final Configuration settings = instance.getSettings();
		
	// create dialog box
	final UIBuilder.Window dialog = builder.new Window(parent, "Skope 3 Gmail Settings",
							   350, 300);
		
	// create welcome label
	UIBuilder.Label welcomeLabel = builder.new 
	    Label("Configure Skope to Gmail Alerts to You", 10, 10, 330, 30);
	dialog.add(welcomeLabel);
		
	// create enabled check box
	final UIBuilder.CheckBox enabledCheckBox = builder.new
	    CheckBox("Enable Emailing of Alerts", 10, 40, 250, 30);
	enabledCheckBox.setSelected(settings.getBooleanValue(
							     "Skope3.Email.Enabled", false));
	dialog.add(enabledCheckBox);
		
	// create user name label
	UIBuilder.Label usernameLabel = builder.new 
	    Label("Username:", 10, 80, 330, 30);
	dialog.add(usernameLabel);
		
	// create user name Field
	final UIBuilder.TextBox usernameBox = builder.new
	    TextBox(100, 75, 240, 35);
	usernameBox.setText(settings.getStringValue(
						    "Skope3.Email.Username", ""));
	dialog.add(usernameBox);
		
	// create password label
	UIBuilder.Label passwordLabel = builder.new 
	    Label("Password:", 10, 120, 330, 30);
	dialog.add(passwordLabel);
		
	// create password Field
	final UIBuilder.TextBox passBox = builder.new
	    TextBox(100, 115, 240, 35);
	passBox.setText(settings.getStringValue(
						"Skope3.Email.Password", ""));
	dialog.add(passBox);
		
	// create recipient label
	UIBuilder.Label recipientLabel = builder.new 
	    Label("Send-To:", 10, 160, 330, 30);
	dialog.add(recipientLabel);
		
	// create recipient Field
	final UIBuilder.TextBox recipientBox = builder.new
	    TextBox(100, 155, 240, 35);
	recipientBox.setText(settings.getStringValue(
						     "Skope3.Email.Destination", ""));
	dialog.add(recipientBox);
		
	// create delay Label
	UIBuilder.Label delayLabel = builder.new 
	    Label("Delay (hrs):", 10, 200, 330, 30);
	dialog.add(delayLabel);
		
	// create recipient Field
	final UIBuilder.TextBox delayBox = builder.new
	    TextBox(100, 195, 240, 35);
	delayBox.setText(Double.toString(settings.getNumberValue(
								 "Skope3.Email.EmailInterval", 43200000) / 1800000));
	dialog.add(delayBox);
		
	// create Save button
	UIBuilder.Button saveButton = builder.new 
	    Button("Save", 100, 240, 150, 30);
	saveButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    String user = usernameBox.getText();
		    String pass = passBox.getText();
		    String recip = recipientBox.getText();
		    double delay = 0;
				
		    try {
			delay = Double.parseDouble(delayBox.getText());
		    } catch (NumberFormatException err) {
			JOptionPane.showMessageDialog(dialog, 
						      "Delay must be a postive numeric value.",
						      "Skope 3", JOptionPane.WARNING_MESSAGE);
			return;
		    }
				
		    // check for positive delay value
		    if(!(delay > 0)) {
			JOptionPane.showMessageDialog(dialog, 
						      "Delay must be a postive numeric value.",
						      "Skope 3", JOptionPane.WARNING_MESSAGE);
			return;
		    }
				
		    // check for incorrect values
		    if(!user.endsWith("@gmail.com") || !recip.contains("@")) {
			JOptionPane.showMessageDialog(dialog, 
						      "Check the information you entered for accurracy. \r\n" +
						      "Username must be a Gmail address, and Send-To must be a valid email.",
						      "Skope 3", JOptionPane.WARNING_MESSAGE);
			return;
		    }
					
		    // save settings
		    boolean serviceEnabled = enabledCheckBox.isSelected();
		    settings.setStringValue("Skope3.Email.Username", user);
		    settings.setStringValue("Skope3.Email.Password", pass);
		    settings.setStringValue("Skope3.Email.Destination", recip);
		    settings.setBooleanValue("Skope3.Email.Enabled", serviceEnabled);
		    settings.setNumberValue("Skope3.Email.EmailInterval", delay * 1800000);
		    instance.getEmailService().setEnabled(serviceEnabled);
		    settings.save(); // commit changes
				
		    // apply changes to service
		    instance.getEmailService().getMailer().setUsername(user);
		    instance.getEmailService().getMailer().setUsername(pass);
		    instance.getEmailService().setRecipient(recip);
				
		    // warn user 
		    if(serviceEnabled) {
			JOptionPane.showMessageDialog(dialog, 
						      "When Skope Gmailer is enabled, all"
						      + "SkopeShots on this computer, will be deleted after being sent."
						      + "The only way to reliably view all images now is to read the emails"
						      + "as they come.", "Skope 3", JOptionPane.WARNING_MESSAGE);
		    }
				
		    // end dialog
		    dialog.dispose();
		}
	    });
	dialog.add(saveButton);
		
	// show the dialog
	dialog.setVisible(true);
    }
}
