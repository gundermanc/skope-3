package com.gundersoft.skope3;

import java.awt.Image;

/**
 * Zoomable image viewer for Skope 3.
 * @author Christian Gunderman
 */
public abstract class ViewerDialog  {
	
	/**
	 * Displays an image in a zoomable dialog box that is 900 pixels
	 * wide, and automatically sets the width to maintain aspect ratio.
	 * @param parent The parent to this dialog box.
	 * @param builder The UI context of this dialog box.
	 * @param image The image to display
	 */
	public static void display(UIBuilder.Window parent, UIBuilder builder, Image image) {
		
		// create dialog
		float ratio = (float)image.getHeight(null) / image.getWidth(null);
		UIBuilder.Window dialog = builder.new Window(parent, "SkopeShot Viewer",
				900, (int)(900 * ratio));
		dialog.setResizable(true);
		
		// create image viewer
		UIBuilder.ImageViewer viewer = builder.new ImageViewer(image);
		dialog.setContentPane(viewer);
		
		// set dialog visible
		dialog.setVisible(true);
	}
}
