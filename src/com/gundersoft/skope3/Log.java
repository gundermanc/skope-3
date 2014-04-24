package com.gundersoft.skope3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Calendar;

/**
 * Simple Log file Opening and Writing Class that supports
 * log appending and automatic management of log file size,
 * to prevent log from getting too big.
 * @author Christian Gunderman
 */
public class Log {
    /** The log file writer */
    private BufferedWriter writer;
    /** Log mode */
    private int logMode;
	
	
    /** Use in the constructor to disable the log completely. */
    public static final int MODE_NONE = 0;
    /** Use in the constructor to enable recording of only errors. */
    public static final int MODE_ERROR_ONLY = 1;
    /** Use in the constructor to enable recording of ALL messages */
    public static final int MODE_COMPLETE = 2;


    /**
     * Creates a new log file context for writing error messages.
     * @param logFile The file to write the log to.
     * @param logFileHeader The header to place at the top of the log
     * file the first time it is written to.
     * @throws IOException Thrown if log file cannot be opened or written
     * to.
     */
    public Log(String logFile, String logFileHeader, int logMode) throws IOException {
	this.logMode = logMode;
		
	// if debug log not disabled, create it
	if(this.logMode != Log.MODE_NONE) {
	    boolean newFile = false;
	    File file = new File(logFile);
			
	    // if log is too big, delete
	    if(file.length() > 40000)
		file.delete();
			
	    // if file doesn't exist, rewrite the log header
	    if (!file.exists())
		newFile = true;
	
	    // open log file
	    this.writer = new BufferedWriter(new FileWriter(logFile, true));
	
	    // write file header
	    if (newFile) {
		writer.write(logFileHeader);
		writer.append("\r\n\r\n");
	    }
	}
    }

    /**
     * Makes a basic log entry of the specified type with the given text.
     * @param type A String telling what type of message this is.
     * @param text The text to display, explaining the warning.
     * @return True if the entry was written successfully, or false
     * if the file cannot be written to.
     */
    protected boolean makeEntry(String type, String... text) {
	// get current date
	Calendar date = Calendar.getInstance();
		
	// write log entry
	try {
	    writer.append("  ");
	    writer.append(type);
	    writer.append(":  ");
	    writer.append((date.get(Calendar.MONTH) + 1)
			  + "/"
			  + date.get(Calendar.DAY_OF_MONTH)
			  + "/"
			  + date.get(Calendar.YEAR)
			  + " "
			  + date.get(Calendar.HOUR)
			  + ":"
			  + (date.get(Calendar.MINUTE) < 10 ? ("0" + date
							       .get(Calendar.MINUTE)) : date.get(Calendar.MINUTE))
			  + ":" + date.get(Calendar.SECOND) + date.get(Calendar.AM_PM));
	    writer.append("    ");
	    for(String s : text) 
		writer.append(s);
	    writer.append("\r\n");
	} catch (IOException e) {
	    return false;		
	}
	return true;
    }
	
    /**
     * Adds a divider to the log to easily differentiate different sections of log.
     */
    public void divider() {
	if(this.logMode != Log.MODE_NONE) {
	    try {
		writer.append("\r\n----------------------------------------" 
			      + "-------------\r\n\r\n");
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
	
    /**
     * Adds a new unindented section header to the log. Useful for differentiating
     * between each program execution or event.
     * @param title The title for the new section header.
     */
    public void section(String title) {
	if(this.logMode != Log.MODE_NONE) {
	    try {
		writer.append("\r\n[");
		writer.append(title);
		writer.append("]\r\n");
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
	
    /**
     * Prints an informational message to the log.
     * @param text The text for the informational message.
     */
    public void i(String... text) {
	if(this.logMode != Log.MODE_NONE 
	   && this.logMode != Log.MODE_ERROR_ONLY)
	    this.makeEntry("INFORM", text);
    }
	
    /**
     * Prints an error message to the log.
     * @param text The error message to be printed.
     */
    public void e(String... text) {
	if(this.logMode != Log.MODE_NONE)
	    this.makeEntry("*ERROR", text);
    }
	
    /**
     * Appends a warning to the log.
     * @param text The warning to be appended.
     */
    public void w(String... text) {
	if(this.logMode != Log.MODE_NONE 
	   && this.logMode != Log.MODE_ERROR_ONLY)
	    this.makeEntry("WARNIG", text);
    }
	
    /**
     * Closes log file and saves buffer contents.
     */
    public void close() {
	if(this.logMode != Log.MODE_NONE) {
	    try {
		this.writer.close();
	    } catch (IOException e) {
		// do nothing
	    }
	}
    }
}
