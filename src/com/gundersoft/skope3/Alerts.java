package com.gundersoft.skope3;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;

/**
 * Contains wrapper functions for dealing with SkopeShots,
 * resetting them, deleting them, and packing them up.
 * @author Christian Gunderman
 */
public class Alerts {
	
    /**
     * Returns a Catalog containing the arrays of Keywords
     * that have alerts and an Image from each to use as a 
     * thumbnail.
     * @return A Catalog containing an overview of the alerts.
     */
    public static Catalog getAlertsOverview() throws IOException {
	File capturesDir = new File("captures");
	File[] keywordFiles = capturesDir.listFiles();
	if(keywordFiles == null)
	    return null;
	Image[] alerts = new Image[keywordFiles.length];
	String[] keywords = new String[keywordFiles.length];
		
	// look through directories for alerts, add them to array
	for(int i = 0; i < alerts.length; i++) {
	    File[] keywordImages = keywordFiles[i].listFiles();
	    if(keywordImages.length > 0) {
		alerts[i] =  ImageIO.read(keywordImages[0]);
		keywords[i] = keywordFiles[i].getName();
	    }
	}
	return new Catalog(keywords, alerts);
    }
	
    /**
     * Gets all the images associated with the specified keyword
     * as well as their file names.
     * @param keyword A keyword from the SkopeService that has
     * triggered SkopeShots.
     * @return Returns a Catalog of the Images.
     * @throws IOException Thrown if unable to open a file.
     */
    public static Catalog getKeywordImages(String keyword) throws IOException {
	File[] keywordImages = new File("captures/" + keyword).listFiles();
	Image[] alerts = new Image[keywordImages.length];
	String[] keywords = new String[keywordImages.length];
		
	for(int i = 0; i < alerts.length; i++) {
	    if(keywordImages.length > 0) {
		alerts[i] =  ImageIO.read(keywordImages[i]);
		keywords[i] = keywordImages[i].getName();
	    }
	}
	return new Catalog(keywords, alerts);
    }
	
    /**
     * Erases the captures directory, and all alerts.
     */
    public static void clearAlerts() {
	File caps = new File("captures");
	Tools.recursiveDelete(caps);
	caps.mkdirs();
    }
	
    /**
     * If the SkopeShot images were previously packed with
     * packAlerts(), the package is deleted. If not, fails
     * silently.
     */
    public static void deleteAlertsPack() {
	File alerts = new File("skope3-alerts.zip");
	if(alerts.exists()) {
	    alerts.delete();
	}
    }
	
    /**
     * Attempts to package all SkopeShots into a zip file for
     * emailing.
     * @throws IOException If unable to open an image for reading.
     */
    public static void packAlerts() throws IOException {
	Tools.ZipWrapper zip = new Tools.ZipWrapper(new File("skope3-alerts.zip"));
		
	// throw in settings file
	zip.putFile(new File("Skope-3.ini"), "SettingsFile.ini");
		
	// recursively add files
	zip.putDirectory(new File("captures"));
		
	// save and close
	zip.close();
    }
	
    /**
     * Gets the date upon which an alert was last triggered.
     * @param keyword The keyword to get the modification date of.
     * @param fileName The file within the alert whose date will be 
     * queried.
     * @return Returns a calendar object with the modification date.
     */
    // TODO: Use this function. Otherwise, delete.
    public static Calendar getAlertDate(String keyword, String fileName) {
	Calendar alertDate = Calendar.getInstance();
	File alertDir = new File("captures/" + keyword + "/" + fileName);
	if(alertDir.exists()) {
	    alertDate.setTimeInMillis(alertDir.lastModified());
	    return alertDate;
	}
	return null;
    }
	
    /**
     * A container object that holds Images and their file names
     * or keywords.
     */
    public static class Catalog {
	/** File names, captions, or keywords */
	public final String[] keywords;
	/** Images */
	public final Image[] images;
		
	/**
	 * Creates a catalog from the given arrays.
	 * @param keywords The captions for the images.
	 * @param images The images to store in the catalog.
	 */
	public Catalog(String[] keywords, Image[] images) {
	    this.keywords = keywords;
	    this.images = images;
	}
    }
}
