package com.gundersoft.skope3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Configuration File Reading and Writing library
 * Reads the specified Config file to a HashTable of values
 * for convenient access.
 * @author Christian Gunderman
 *
 */
public class Configuration {
    /* HashTable of Settings Values */
    private Hashtable<String, String> table;
    /* Name of Settings file */
    private String fileName;
    /* Text Comment beginning/ending with [] put in Config file */
    private String fileHeader;

    /**
     * Constructs the object and initializes a HashTable of Values. 
     * Using the functions attached to this class, you can write to this
     * HashTable and the values will be automatically saved to the file
     * upon calling the save() method.
     * @param fileName Path to config file.
     * @param fileHeader Comment to place at the top of the Config file.
     */
    public Configuration(String fileName, String fileHeader) {
	this.fileName = fileName;
	this.fileHeader = fileHeader;
	// declare a new HashTable with a basic java API hashing function
	table = new Hashtable<String, String>();

	// if no comment provided, put the default one
	if (this.fileHeader == null) {
	    this.fileHeader = "[\n  Conf Configuration File Reader/Writer\n"
		+ "  (C)2013 C. Gunderman\n  CAUTION: This file wi"
		+ "ll be overwritten\n]\n";
	}
		
	cacheFile(); // cache contents of file in the HashTable
    }

    /**
     * Reads settings in from the settings file and stores them in the HashTable
     */
    private void cacheFile() {
	FileReader reader;
	try {
	    reader = new FileReader(fileName);
	} catch (FileNotFoundException e) {
	    return; // no error handling, default config used instead
	}

	StringBuilder buffer = new StringBuilder();
	String key = null;
	String value = null;
	boolean inComment = false;
	int c;

	// while chars remain
	try {
	    while ((c = reader.read()) != -1) {
		if (key == null && c == '[')
		    inComment = true;
		else if (key == null && c == ']')
		    inComment = false;
		else if (inComment) {
		    // do nothing
		} else if (key == null && c == '=') {
		    key = buffer.toString();
		    buffer = new StringBuilder();
		} else if (key != null && c == '\n') {
		    value = buffer.toString();
		    table.put(key, value);
		    buffer = new StringBuilder();
		    key = null;
		    value = null;
		} else if ((key == null && c != '\n' && c != '\r' && c != ' ' && c != '\t')
			   || key != null)
		    buffer.append((char) c);
	    }

	    // handle any items that were not followed by a new line
	    if (key != null) {
		value = buffer.toString();
		table.put(key, value);
	    }
	    reader.close();
	} catch (IOException e) {
	    return;
	}
    }
	
    /**
     * Deletes the specified value from the configuration values 
     * hash table. Will be removed from file upon next call to save().
     * @param key The key at which the value to be removed is stored.
     */
    public void deleteValue(String key) {
	this.table.remove(key);
    }
	
    /**
     * Change comment header at top of config file. These changes will be reflected
     * next time save() or saveACopy() is called.
     * @param fileHeader The new comment to put at beginning of the Config document.
     */
    public void setFileHeader(String fileHeader) {
	this.fileHeader = fileHeader;
    }
	
    /**
     * Gets the header given when this object was created, or upon the last call to
     * setFileHeader().
     * @return A String containing the last given fileHeader.
     */
    public String getFileHeader() {
	return this.fileHeader;
    }
	
    /**
     * Searches HashTable for a value stored at the specified key. If one does
     * not exist, the function returns the value of default value instead.
     * @param key The key representing the value being searched for.
     * @param defaultValue The value to return instead if this object doesn't 
     * have a stored value for the specified key.
     * @return Returns the String value stored with the specified key in the 
     * config document, or defaultValue if no such value exists.
     */
    public String getStringValue(String key, String defaultValue) {
	String result = table.get(key);
	if(result != null)
	    return result;
	return defaultValue;
    }
	
    /**
     * Returns a numeric representation of the value stored at the specified key.
     * If the configuration document does not have the specified key, then default
     * value will be returned instead.
     * @param key The key who's value will be searched for.
     * @param defaultValue The value returned if the given key does not exist in
     * the config document.
     * @return The value stored in the given key, or defaultValue if the key does
     * not exist in the config file.
     */
    public double getNumberValue(String key, double defaultValue) {
	String result = table.get(key);
	if(result != null) {
	    try{
		return Double.parseDouble(result);
	    } catch (NumberFormatException e) {
		return defaultValue;
	    }
	}
	return defaultValue;
    }
	
    /**
     * Sets the value of the given key to the newly given value. In the hashtable.
     * These changes will immediately affect any code that call getStringValue or
     * getNumberValue to get settings, but they will not persist across program 
     * executions unless save() is called, saving the settings to the config file.
     * @param key The key who's value to change.
     * @param value The new value to store in the given key.
     * @return Returns the value previously stored in the given key, or 0 if 
     * there was no value originally.
     */
    public double setNumberValue(String key, double value) {
	String oldValue = table.put(key, Double.toString(value));
	if(oldValue != null) {
	    try {
		return Double.parseDouble(oldValue);
	    } catch (NumberFormatException e) {
		return 0;
	    }
	}
	return 0;
    }
	
    /**
     * Sets the specified key to a boolean value.
     * @param key The key who's value to modify.
     * @param value A boolean value to be stored in the
     * given key.
     * @return True if the previous value was a boolean true value, and
     * false if the previous value was false, or if there was no previous
     * value, or previous value is of non-boolean type.
     */
    public boolean setBooleanValue(String key, boolean value) {
	String oldValue = table.put(key, (value ? "true":"false"));
	if(oldValue != null && oldValue.equals("true"))
	    return true;
	return false; // default false, no matter what value is
    }
	
    /**
     * Opens the value associated with the given key. If value was previously written to
     * with setBooleanValue() then value is read in as a boolean and the result, true or
     * false, is returned with the function. If the value does not exist in the document,
     * or is not of boolean type, defaultValue is 
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBooleanValue(String key, boolean defaultValue) {
	String result = table.get(key);
	if(result != null) {
	    if(result.equals("true"))
		return true;
	    else if (result.equals("false"))
		return false;
	}
	return defaultValue;
    }
	
    /**
     * Sets the value of the given key to the newly given value. In the hashtable.
     * These changes will immediately affect any code that call getStringValue or
     * getNumberValue to get settings, but they will not persist across program 
     * executions unless save() is called, saving the settings to the config file.
     * @param key The key who's value to change.
     * @param value The new value to store in the given key.
     * @return Returns the value previously stored in the given key, or null if 
     * there was no value originally.
     */
    public String setStringValue(String key, String value) {
	return table.put(key, value);
    }
	
    /**
     * Saves the values in the Conf object to the config file who's path 
     * was given at object creation.
     * @return Returns true if file is written successfully, false if an IO
     * error occurs.
     */
    public boolean save() {
	return saveACopy(this.fileName);
    }
	
    /**
     * Saves all of the values in the Conf object to the given config
     * file path.
     * @param fileName The file to save the settings to.
     * @return Returns true if the operation succeeds, and false if IO
     * error occurs.
     */
    public boolean saveACopy(String fileName) {
	try {
	    FileWriter writer = new FileWriter(fileName);
	    if(this.fileHeader != null)
		writer.write(this.fileHeader);
			
	    // put each item from the HashTable into the file
	    Enumeration<String> keys = table.keys();
	    while(keys.hasMoreElements()) {
		String key = keys.nextElement();
		writer.write(key);
		writer.write('=');
		writer.write(table.get(key));
		writer.write('\n');
	    }
			
	    writer.close();
	    return true;
	} catch (IOException e) {
	    return false;
	}
    }
}
