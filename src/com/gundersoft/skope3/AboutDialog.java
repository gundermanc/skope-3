package com.gundersoft.skope3;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Contains information about Skope 3 and yours truly ;) and 
 * provides some basic license information in the form of an
 * AboutDialog that can be called statically and displayed.
 * @author Christian Gunderman
 */
public class AboutDialog {
    /** Version String displayed in program */
    public static final String versionText = "Skope 3 ALPHA RC 3";
    /** Author String displayed in program */
    public static final String authorText = "(C) 2013 Christian Gunderman";
    /** Text displayed in AboutDialog scrollable text area */
    public static final String copyrightText = "License Info and Acknowledgements:"
	+ "\r\n\r\nSkope is made possible by the work of the JNativeHook Team. "
	+ "JNativeHook provides the keyboard monitoring functionality that makes "
	+ "Skope keylogger possible on all computer platforms. \r\n\r\n"
	+ "Furthermore, I would like to thank all of the original Skope version "
	+ "1 users for providing valuable feedback that led to the creation of "
	+ "this completely new generation of Skope.\r\n"
	+ "Skope is provided to you free of charge and is subject to the GNU GPL v3"
	+ "By using this work, you agree to release Christian Gunderman from any "
	+ "and all liability and assume full responsiblities for the consequences "
	+ "of this program's use.\r\n\r\nFurthermore, according to the GPL, you "
	+ "have certains rights to this code and may create derived works. However, "
	+ "I implore you to help me improve upon this project and join the effort "
	+ "to maintain this code, rather than starting your own variation. By "
	+ "contributing to this project, you are helping create a great, free "
	+ "product for the community.\r\n\r\nIf you do not have a copy of the "
	+ "latest GNU GPL, it can be obtained by writing to the Free Software "
	+ "foundation.";
	
    /**
     * Displays about dialog box with copyright information from
     * copyrightText variable.
     * @param parent The parent window to disable during showing this modal dialog.
     * @instance The current instance of Skope and all of its packaged variables.
     */
    public static void displayAboutBox(UIBuilder.Window parent, Main instance) {
		
	// create dialog
	UIBuilder.Window aboutDialog = instance.getBuilder().new Window(parent, "About Skope 3", 250, 200);
		
	// create title label
	UIBuilder.Label aboutLabel = instance.getBuilder().new 
	    Label(versionText, 10, 10, 240, 25);
	aboutDialog.add(aboutLabel);
		
	// create copyright label
	UIBuilder.Label copyrightLabel = instance.getBuilder().new 
	    Label(authorText, 10, 35, 240, 25);
	aboutDialog.add(copyrightLabel);
		
	// create text area
	JTextArea textArea = new JTextArea();
	textArea.setWrapStyleWord(true);
	textArea.setLineWrap(true);
	textArea.setText(copyrightText);
	textArea.setEditable(false);
	JScrollPane scroller = new JScrollPane(textArea);
	scroller.setLocation(10, 70);
	scroller.setSize(230, 100);
	aboutDialog.add(scroller);
	textArea.setCaretPosition(0);
		
	aboutDialog.setVisible(true);
	aboutDialog.dispose();
    }
}
