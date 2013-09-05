package com.gundersoft.skope3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * Implements dialog boxes for the creation, deletion, and editing of Skope
 * Service keywords.
 * @author Christian Gunderman
 */
public class KeywordDialog {
	
	/**
	 * Displays the keywords list dialog box.
	 * @param parent The parent of this dialog.
	 * @param instance This instance of Skope 3.
	 */
	public static void displayKeywordsBox(java.awt.Window parent, final Main instance) {
		// create dialog
		final UIBuilder.Window dialog = instance.getBuilder().new Window(parent, "SkopeShot Keywords", 340, 200);
		
		// create keywords list
		final UIBuilder.List list = instance.getBuilder().new List(0, 0, 320, 120);
		final JScrollPane listScroller = new JScrollPane(list);
		listScroller.setSize(320, 120);
		listScroller.setLocation(10, 10);
		dialog.add(listScroller);
		String[] keywords = instance.getService().getKeywordArray();
		for(String s : keywords)
			list.getDefaultListModel().add(list.getDefaultListModel().size(), s);
		
		// edit button
		UIBuilder.Button editButton = 
				instance.getBuilder().new Button("Edit", 100, 140, 80, 25);
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Service.Keyword keyword = instance.getService().
						getKeyword((String)(list.getSelectedValue()));
				if(keyword != null)
					displayEditKeywordBox(dialog, instance, keyword);
			}
		});
		dialog.getContentPane().add(editButton);
		
		// new button
		UIBuilder.Button newButton = instance.getBuilder().new Button("New", 10, 140, 80, 25);
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayEditKeywordBox(dialog, instance, null);
				
				// reload list from service
				list.getDefaultListModel().clear();
				String[] keywords = instance.getService().getKeywordArray();
				for(String s : keywords)
					list.getDefaultListModel().add(list.getDefaultListModel().size(), s);
			}
		});
		dialog.add(newButton);

		// delete button
		UIBuilder.Button delButton = instance.getBuilder().new Button("Delete", 190, 140, 140, 25);
		delButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selection = (String)list.getSelectedValue();
				if(selection != null && 
						JOptionPane.showConfirmDialog(dialog, 
								"Are you sure that you want to delete this keyword?", 
								"Confirm Delete Keyword", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					list.getDefaultListModel().remove(list.getSelectedIndex());
					instance.getService().deleteKeyword(selection);
					
					// update list
					list.getDefaultListModel().clear();
					String[] keywords = instance.getService().getKeywordArray();
					for(String s : keywords) {
						list.getDefaultListModel().add(list.getDefaultListModel().size(), s);
					}
				}
			}
		});
		dialog.add(delButton);
		
		// set dialog visible
		dialog.setVisible(true);
		
		dialog.dispose();
	}
	
	/**
	 * Displays the dialog box for editing a keyword.
	 * @param parent The dialog that spawned this one.
	 * @param instance This instance of Skope 3.
	 * @param keyword The keyword to edit.
	 */
	public static void displayEditKeywordBox(final java.awt.Window parent, final Main instance, 
			final Service.Keyword keyword) {
		
		// create dialog
		final UIBuilder.Window dialog = instance.getBuilder().new Window(parent, "Edit/New Keyword", 380, 200);
		
		// text label
		final UIBuilder.Label mainLabel = instance.getBuilder().new 
				Label("Edit Keyword and Press Ok to Save", 10, 10, 320, 25);
		dialog.add(mainLabel);
		
		// keyword text label
		final UIBuilder.Label keyLabel = instance.getBuilder().new 
				Label("Keyword: ", 10, 50, 100, 25);
		dialog.add(keyLabel);
		
		// keyword text box
		final UIBuilder.TextBox keywordBox = instance.getBuilder().new 
				TextBox(100, 45, 150, 30);
		if(keyword != null)
			keywordBox.setEditable(false);
		dialog.add(keywordBox);
		
		// delay text label
		final UIBuilder.Label delayLabel = instance.getBuilder().new 
				Label("Delay (m/s): ", 10, 80, 100, 25);
		dialog.add(delayLabel);
				
		// delay text box
		final UIBuilder.TextBox delayBox = instance.getBuilder().new 
				TextBox(100, 75, 150, 30);
		dialog.add(delayBox);
		
		// number text label
		final UIBuilder.Label numLabel = instance.getBuilder().new 
				Label("Number: ", 10, 110, 100, 25);
		dialog.add(numLabel);
		
		// number text box
		final UIBuilder.TextBox numberBox = instance.getBuilder().new 
				TextBox(100, 105, 150, 30);
		dialog.add(numberBox);
		
		// OK Button
		final UIBuilder.Button okButton = instance.getBuilder().new Button("Ok", 120, 145, 75, 25);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String keyword = keywordBox.getText();
				int delay = 0;
				int number = 0;
				
				try {
					delay = Integer.parseInt(delayBox.getText());
					number = Integer.parseInt(numberBox.getText());
				} catch (NumberFormatException err) {
					JOptionPane.showMessageDialog(dialog, "Error! Delay and number must "
							+ "both be positive integer values.");
					return;
				}
				
				if(delay < 1 || number < 1 || keyword.length() < 2) {
					JOptionPane.showMessageDialog(dialog, "Error! Delay and number must "
							+ "both be positive integer values and keyword must be > 2 characters.");
					return;
				}
				
				instance.getService().registerKeyword(keyword, delay, number);
				
				dialog.dispose();
			}
		});
		dialog.add(okButton);
		
		// load previous values
		if(keyword != null) {
			keywordBox.setText(keyword.getKeyword());
			delayBox.setText(Integer.toString(keyword.getDelay()));
			numberBox.setText(Integer.toString(keyword.getNumber()));
		} else {
			Service.Keyword defaults = instance.getService().getDefaults();
			delayBox.setText(Integer.toString(defaults.getDelay()));
			numberBox.setText(Integer.toString(defaults.getNumber()));
		}
		
		dialog.setVisible(true);
		dialog.dispose();
	}
}
