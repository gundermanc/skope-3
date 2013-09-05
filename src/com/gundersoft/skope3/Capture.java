package com.gundersoft.skope3;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;

/**
 * Utility class that wraps the functions necessary to produce screen shots.
 * @author Christian Gunderman
 */
public class Capture {
	
	/**
	 * Takes a screenshot of the desktop and scales it by the given quality
	 * decimal.
	 * @param quality Value between 0 and 100
	 * @return A bufferedimage containing the screenshot
	 */
	public static BufferedImage capture(double quality) {
		Robot robot;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			return null; // AWT Exception
		}
		
		// impossible quality setting
		if(quality > 1 || quality <= 0)
			return null;
		
		BufferedImage capture = robot.createScreenCapture(new Rectangle(
				Toolkit.getDefaultToolkit().getScreenSize()));
		BufferedImage newImage = new BufferedImage((int)(quality * capture.getWidth()), 
				(int)(quality * capture.getHeight()), BufferedImage.TYPE_INT_ARGB);
		Image scaledImage = capture.getScaledInstance((int)(quality * capture.getWidth()), 
				(int)(quality * capture.getHeight()), Image.SCALE_DEFAULT);
		newImage.createGraphics().drawImage(scaledImage, 0, 0, newImage.getWidth(), 
				newImage.getHeight(), null);
		return newImage;
	}
	
	/**
	 * Quickly and easily saves the image to a file in the specified format.
	 * Leave format null for default PNG.
	 * @param image ScreenShot object.
	 * @param format Leave null for PNG.
	 * @return True if the write operation is a success
	 */
	public static boolean saveCapture(BufferedImage image, String fileName, String format) {
		if(format == null)
			format = "png";
		try {
			return ImageIO.write(image, format, new File(fileName + '.' + format));
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * ScreenShot capturing robot that creates new capture thread and takes
	 * screen shots in the background.
	 */
	public static class PicRobot implements Runnable {
		/** Keyword that tells PicRobot where to store the image */
		private String hint;
		/** Delay between shots in millis */
		private int delay;
		/** Number of shots to take in this object */
		private int number;
		/** New thread that will run the Robot */
		private Thread thread;
		
		/**
		 * Creates an automatic picture taking robot that will 
		 * capture screenshots on a background thread.
		 * @param hint The directory to store the taken images in.
		 * @param delay How many milliseconds between each shot.
		 * @param number Number of shots to take.
		 */
		public PicRobot(String hint, int delay, int number) {
			this.hint = hint;
			this.delay = delay;
			this.number = number;
			
			// create thread, but don't start yet
			this.thread = new Thread(this);
		}
		
		/**
		 * Starts the thread and begins the PicRobot's Image Capture
		 * sequence.
		 */
		public void start() {
			this.thread.start();
		}

		/**
		 * The main function for the PicRobot thread, this will be called when
		 * the PicRobot is started with the start() function. It should not
		 * be called by the user since it will cause the main thread to freeze
		 * until all images are Captured.
		 */
		@Override
		public void run() {
			Calendar date = Calendar.getInstance();
			File directory = new File(hint);
			for(int i = 0; i < this.number; i++) {
				if(!directory.exists())
					directory.mkdirs();
				saveCapture(capture(1), this.hint + "//" 
						+ (date.get(Calendar.MONTH) + 1) + "-" 
						+ date.get(Calendar.DAY_OF_MONTH) + "-" 
						+ date.get(Calendar.YEAR) + "- " + date.get(Calendar.HOUR) 
						+ "-" + date.get(Calendar.MINUTE) + "-" 
						+ date.get(Calendar.SECOND)	+ "-" + date.get(Calendar.MILLISECOND) 
						+ date.get(Calendar.AM_PM) + "; #" + i, null);
				try {
					Thread.sleep(this.delay);
				} catch (InterruptedException e) {
					System.out.println("Foo!");
					return; 
				}
			}
		}
	}
}
