package com.gundersoft.skope3;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Handles security and password authentication for the User interface.
 * @author Christian Gunderman
 */
public class Security {
    /** The hash for the current password */
    private String passwordHash;
	
    /**
     * Creates an instance of Security and stores the hash for
     * later authentication.
     * @param passwordHash The hash, loaded from file. Null for none.
     * If hash is null, all password authentications will automatically 
     * succeed.
     */
    public Security(String passwordHash) {
	this.passwordHash = passwordHash;
    }
	
    /**
     * Checks to see if there is a password set.
     * @return True if there is a password, and false if not.
     */
    public boolean isHashPresent() {
	return passwordHash != null;
    }
	
    /**
     * Hashes password attempt and checks against stored hash. 
     * @param password The password attempt, typed by the user.
     * @return Returns true if the password is correct, and false
     * if incorrect.
     */
    public boolean auditPassword(String password) {
	if(this.passwordHash == null)
	    return true;
	try {
	    return hash(password).equals(this.passwordHash);
	} catch (NoSuchAlgorithmException e) {
	    return false;
	}
    }
	
    /**
     * Sets the password for this security context. If the password
     * is null, password is removed and all audits will automatically succeed.
     * @param New password.
     */
    public void setPassword(String password) {
	if(password != null && password.length() > 0) {
	    try {
		this.passwordHash = hash(password);
	    } catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else
	    this.passwordHash = null;
    }
	
    /**
     * Gets the current password hash.
     * @return The current password hash.
     */
    public String getPasswordHash() {
	return this.passwordHash;
    }
	
    /**
     * Hashes a String into a SHA-256 secure hash for secure password storage.
     * @param password The text to hash.
     * @return The hashed text.
     * @throws NoSuchAlgorithmException Thrown if the current platform does not
     * have a SHA-256 implementation.
     */
    public static String hash(String password) throws NoSuchAlgorithmException {
	MessageDigest sha256 = MessageDigest.getInstance("SHA-256");        
	StringBuilder buffer = new StringBuilder();
	byte[] passBytes = password.getBytes();
	byte[] passHash = sha256.digest(passBytes);
	for(byte b : passHash)
	    buffer.append(b);
	return buffer.toString();
    }
	
    /**
     * Displays a password dialog, prompting the user for their password. 
     * @param parent The parent of the new dialog.
     * @param builder The current user interface context.
     * @return If hashed user given password matches the stored password 
     * hash, returns true.
     */
    public boolean auditPasswordDialog(UIBuilder.Window parent, final UIBuilder builder) {
	return passwordDialog(false, parent, builder);
    }
	
    /**
     * Prompts the user for a new password.
     * @param parent The parent of the password dialog.
     * @param builder The current user interface.
     * @return True if password set successfully.
     */
    public boolean resetPasswordDialog(UIBuilder.Window parent, final UIBuilder builder) {
	return passwordDialog(true, parent, builder);
    }
	
    /**
     * Checks given password against hashes, or sets new password.
     * @param resetPassword If true, resets password. If false, audits password
     * and if it matches, returns true.
     * @param parent Parent dialog.
     * @param builder Current user interface.
     * @return Returns true if passwords match, in normal mode, or returns true
     * if password is successfully set in resetPassword mode.
     */
    private boolean passwordDialog(final boolean resetPassword, UIBuilder.Window parent, final UIBuilder builder) {
		
	// if no password is present, audit automatic success
	if(!this.isHashPresent() && !resetPassword)
	    return true;
		
	// Creates new Result storage object to get around Java limitations
	final Result result = new Result();
		
	// create dialog
	final UIBuilder.Window dialog = builder.new Window(parent,
							   resetPassword ? "Skope 3 - Reset password":
							   "Skope 3 - Security Audit",200, 150);
		
	// label
	final UIBuilder.Label welcomeLabel = builder.new 
	    Label(resetPassword ? "Enter new password:":"Enter your password:", 
		  10, 10, 150, 25);
	dialog.add(welcomeLabel);
		
	// password field
	final UIBuilder.TextBox passBox = builder.new TextBox(10, 45, 180, 30);
	passBox.setPasswordMaskEnabled(true);
	dialog.add(passBox);
		
	// OK button
	UIBuilder.Button okButton = builder.new Button(resetPassword ? "Save":"OK", 55, 85, 90, 30);
	okButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    if(resetPassword) {
			if(passBox.getText().isEmpty()) {
			    if(JOptionPane.showConfirmDialog(dialog,
							     "Are you sure you do not want a password?" , "Skope 3", 
							     JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 
			       JOptionPane.YES_OPTION) {
				Security.this.setPassword(null);
				dialog.setVisible(false);
			    } 
			    return;							
			} else {
			    Security.this.setPassword(passBox.getText());
			    dialog.setVisible(false);
			}
		    } else {
			if(Security.this.auditPassword(passBox.getText())) {
			    result.value = true;
			    dialog.setVisible(false);
			} else {
			    Timer incorrectTimer = new Timer(2000, new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
					passBox.setBackground(Color.WHITE);
					welcomeLabel.setText("Enter your password:");
					((Timer)e.getSource()).stop();
				    }
				});
			    incorrectTimer.start();
			    passBox.setBackground(Color.RED);
			    passBox.setText("");
			    welcomeLabel.setText("Incorrect Password");
			}
		    }
				
		}
	    });
	dialog.getRootPane().setDefaultButton(okButton);
	dialog.add(okButton);
	dialog.setVisible(true);
	return result.value;
    }
	
    /**
     * Stores function result. Used to get around Java final
     * variable requirements.
     */
    private static class Result {
	/** Result of the Function */
	private boolean value;
    }
}
