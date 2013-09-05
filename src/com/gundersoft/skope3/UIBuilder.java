package com.gundersoft.skope3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A set of extended Swing classes, allowing for increased functionality,
 * new controls, and easy creation of controls following a set, preloaded
 * theme, a.k.a. "UIBuilder".
 * @author Christian Gunderman
 */
public class UIBuilder {
	/*
	 * UI Builder Fields
	 */
	/** The directory containing all theme files */
	private String themeDirectory;
	/** The image to use as the default icon for all windows */
	private Image iconImage;
	/** The image to use as the default background for all windows */
	private Image backgroundImage;
	/** The image to be used for texturing buttons */
	private Image buttonImage;
	/** The image used to texture large buttons */
	private Image bigButtonImage;
	/** The image used to texture text box fields */
	private Image textBoxImage;
	/** The image used to draw checkboxes in the current theme */
	private Image checkboxImage;
	/** The left arrow in the ImageShuffler control theme */
	private Image leftArrow;
	/** The right arrow in the ImageShuffler control */
	private Image rightArrow;
	/** The image for semitransparent overlays */
	private Image transparency;
	
	/*
	 * Label Fields
	 */
	/** Enable shadows beneath text on label controls */
	private boolean labelShadowEnabled;
	/** Sets the default color of label shadows */
	private Color labelShadowColor;
	/** Sets the default shadow offset from the original text */
	private int labelShadowOffset;
	
	/*
	 * Image shuffler fields
	 */
	/** Enable shadow on text in the ImageShuffler control */
	private boolean shufflerShadowEnabled;
	/** Sets text shadow color for ImageShuffler */
	private Color shufflerShadowColor;
	/** Sets how many pixels diagonal the shadow will be away */
	private int shufflerShadowOffset;
	
	/*
	 * All control defaults
	 */
	/** default text color for all controls */
	private Color defaultForeground;
	
	/**
	 * Constructs a UIBuilder, which acts as a template for all controls.
	 * Use UIBuilder methods to set the defaults and load the theme for 
	 * this program and all created controls will follow this theme.
	 * @param themeDirectory The directory to load theme images from.
	 * Theme directory should contain all of the images in the code below
	 * to successfully texture all controls. 
	 * @throws IOException Thrown if a theme image file could not be opened
	 * or read successfully.
	 */
	public UIBuilder(String themeDirectory) throws IOException {
		// store theme directory
		if(themeDirectory.charAt(themeDirectory.length() - 1) == '/' ||
				themeDirectory.charAt(themeDirectory.length() - 1) == '\\')
			this.themeDirectory = themeDirectory.substring(0, themeDirectory.length() - 1);
		else
			this.themeDirectory = themeDirectory;
		
		// cache images in object
		this.iconImage = ImageIO.read(
				new File(this.themeDirectory + "/icon.png"));
		this.backgroundImage = ImageIO.read(
				new File(this.themeDirectory + "/bg.png"));
		this.buttonImage = ImageIO.read(
				new File(this.themeDirectory + "/button.png"));
		this.bigButtonImage = ImageIO.read(
				new File(this.themeDirectory + "/bigbutton.png"));
		this.textBoxImage = ImageIO.read(
				new File(this.themeDirectory + "/textbox.png"));
		this.checkboxImage = ImageIO.read(
				new File(this.themeDirectory + "/checkbox.png"));
		this.leftArrow = ImageIO.read(
				new File(this.themeDirectory + "/leftarrow.png"));
		this.rightArrow = ImageIO.read(
				new File(this.themeDirectory + "/rightarrow.png"));
		this.transparency = ImageIO.read(
				new File(this.themeDirectory + "/transparent.png"));
		
		// set default label values
		this.labelShadowEnabled = true;
		this.labelShadowColor = Color.BLACK;
		this.labelShadowOffset = 2;
		
		// set default shuffler values
		this.shufflerShadowEnabled = true;
		this.shufflerShadowColor = Color.BLACK;
		this.shufflerShadowOffset = 2;
		
		// set global defaults
		this.defaultForeground = Color.WHITE;
	}
	
	/**
	 * Sets the default Window Icon for this UIBuilder to be the
	 * given Image. All subsequent windows will be created with this
	 * icon, but already built ones will remain the same.
	 * @param iconImage New Window Icon Image.
	 */
	public void setIconImage(Image iconImage) {
		this.iconImage = iconImage;
	}
	
	/**
	 * Gets the current Image set to be the default Icon for each new
	 * Window created by this builder.
	 * @return The current icon image
	 */
	public Image getIconImage() {
		return this.iconImage;
	}
	
	/**
	 * Sets the default value for ImageShuffler shadows to the value of the
	 * given boolean. Any subsequently created ImageShufflers will have text
	 * shadows if enabled, and will not if disabled.
	 * @param shufflerShadowEnabled True enables shadows, false disables 
	 * text shadowing for all new shufflers.
	 */
	public void setShufflerShadowEnabled(boolean shufflerShadowEnabled) {
		this.shufflerShadowEnabled = shufflerShadowEnabled;
	}
	
	/**
	 * Gets a boolean determining whether all new ImageShufflers have text
	 * shadows enabled by default.
	 * @return True if ImageShufflers have text shadows enabled by default,
	 * and false if not.
	 */
	public boolean isShufflerShadowEnabled() {
		return this.shufflerShadowEnabled;
	}
	
	/**
	 * Sets the default color for ImageShuffler shadows. All new ImageShufflers
	 * will be created with text shadows of this color.
	 * @param shufflerShadowColor The color for ImageShuffler shadows.
	 */
	public void setShufflerShadowColor(Color shufflerShadowColor) {
		this.shufflerShadowColor = shufflerShadowColor;
	}
	
	/**
	 * Gets the default color for all new ImageShufflers text shadows.
	 * @return The color new ImageShufflers text will be shadowed with.
	 */
	public Color getShufflerShadowColor() {
		return this.shufflerShadowColor;
	}
	
	/**
	 * Sets the number of pixels diagonal to the original text that
	 * any new ImageShuffler's shadow will be by default.
	 * @param shufflerShadowOffset
	 */
	public void setShufflerShadowOffset(int shufflerShadowOffset) {
		this.shufflerShadowOffset = shufflerShadowOffset;
	}
	
	/**
	 * Returns the number of pixels diagonal from the original text
	 * that any new ImageShuffler's shadow will be by default.
	 * @return
	 */
	public int getShufflerShadowOffset() {
		return this.shufflerShadowOffset;
	}
	
	/**
	 * Enables text shadows on all Labels subsequently created.
	 * @param labelShadowEnabled True enables shadows, false 
	 * disables them.
	 */
	public void setLabelShadowEnabled(boolean labelShadowEnabled) {
		this.labelShadowEnabled = labelShadowEnabled;
	}
	
	/**
	 * Gets whether subsequently created Labels will have text
	 * shadows or not.
	 * @return True, all new Labels will have text shadows. False,
	 * no shadows.
	 */
	public boolean isLabelShadowEnabled() {
		return this.labelShadowEnabled;
	}
	
	/**
	 * Sets the default color for all new Label shadows. Any Labels
	 * subsequently created will express this trait.
	 * @param labelShadowColor Color for shadow of new Labels.
	 */
	public void setLabelShadowColor(Color labelShadowColor) {
		this.labelShadowColor = labelShadowColor;
	}
	
	/**
	 * Gets the default color for all new Label text shadows.
	 * @return A Color object, representing the label Color.
	 */
	public Color getLabelShadowColor() {
		return this.labelShadowColor;
	}
	
	/**
	 * Sets the default offset for the text shadow for all new 
	 * Label objects.
	 * @param labelShadowOffset How many pixels diagonal the shadow
	 * will be from the original text.
	 */
	public void setLabelShadowOffset(int labelShadowOffset) {
		this.labelShadowOffset = labelShadowOffset;
	}
	
	/**
	 * Gets the default number of pixels from the original text
	 * that the Label text shadow is drawn for all new Label
	 * objects.
	 * @return Number of pixels diagonal from the original text.
	 */
	public int getLabelShadowOffset() {
		return this.labelShadowOffset;
	}
	
	/**
	 * Sets the default text color for all UIBuilder controls.
	 * @param defaultForeground The default text Color that
	 * will be expressed by all subsequently created controls.
	 */
	public void setDefaultForeground(Color defaultForeground) {
		this.defaultForeground = defaultForeground;
	}
	
	/**
	 * Returns the default text Color that any subsequently created
	 * UIBuilder controls will have.
	 * @return
	 */
	public Color getDefaultForeground() {
		return this.defaultForeground;
	}
	
	/**
	 * A replacement ContentPane for JDialogs, Windows, JWindows and JFrames
	 * that allows for the drawing of background images, while still allowing
	 * for resident controls in the parent window. It is recommended that you
	 * go ahead and use the UIBuilder.Window class, rather than sticking this
	 * in a JFrame because it is already built for you and is harder to mess
	 * up.
	 */
	@SuppressWarnings("serial")
	public class BackgroundPanel extends JPanel {
		/** An Image object that will be drawn in the background */
		protected Image backgroundImage;
		
		/**
		 * Constructs the background panel with the specified background Image.
		 * Use Window.setContentPane(backgroundPanel) to add this panel to your
		 * window. Add controls with backgroundPanel.add().
		 * @param backgroundImage The image to draw to the background panel. The
		 * background panel does not do anything fancy with the drawing, but rather,
		 * just draws the image stretched to the window dimensions. If null, the
		 * backgroundPanel will just assume the default UIBuilder background.
		 */
		public BackgroundPanel(Image backgroundImage) {
			super();
			
			// if no background image is given, set to UIBuilder default.
			if(backgroundImage == null)
				this.backgroundImage = UIBuilder.this.backgroundImage;
			else
				this.backgroundImage = backgroundImage;
			
			// remove layout so that x, y coords. can be used
			this.setLayout(null);
		}
		
		/**
		 * Changes the background of this BackgroundPanel to the image
		 * given and repaints the control.
		 * @param backgroundImage New background image for the panel.
		 */
		public void setBackgroundImage(Image backgroundImage) {
			this.backgroundImage = backgroundImage;
			this.repaint();
		}
		
		/**
		 * Gets the current Background Image.
		 * @return The Image object being painted to the panel.
		 */
		public Image getBackgroundImage() {
			return this.backgroundImage;
		}
		
		/**
		 * Called by the system, this function repaints the panel
		 * whenever part of it is invalidated. There is no reason
		 * for the user to call this. Use repaint() instead, if you
		 * wish to update the panel's paint.
		 */
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(this.getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if(this.backgroundImage != null) {
				g.drawImage(this.backgroundImage, 0, 0, 
						this.getWidth(), this.getHeight(), 0, 0, 
						this.backgroundImage.getWidth(null),
						this.backgroundImage.getHeight(null), null);
			}
		}
	}
	
	/**
	 * A replacement ContentPane, or just general control for 
	 * UIBuilder.Window that allows for zooming of background images by clicking.
	 */
	@SuppressWarnings("serial")
	public class ImageViewer extends BackgroundPanel {
		/** What fraction of the image size is the zoom box */
		protected float zoomBoxFraction;
		/** What color is the text in the zoom box */
		protected Color zoomBoxTextColor;
		/** How far from the edge of the photo should rendering begin */
		protected int imageXOffset;
		/** How far from top of the photo should rendering begin */
		protected int imageYOffset;
		/** How many times is the image magnified. 1 is normal size */
		protected float zoomLevel;
		/** Maximum number of times a photo can be magnified.*/
		protected float maxZoomLevel;
		/** Minimum zoom that a picture can have. */
		protected float minZoomLevel;
		
		/**
		 * Creates a new ImageViewer panel with the specified image as the
		 * background.
		 * @param backgroundImage The image to display in the viewer.
		 */
		public ImageViewer(Image backgroundImage) {
			
			// create parent class
			super(backgroundImage);
			
			// store fields
			this.zoomBoxFraction = 0.75f;
			this.zoomBoxTextColor = UIBuilder.this.defaultForeground;
			this.imageXOffset = 0;
			this.imageYOffset = 0;
			this.zoomLevel = 1;
			this.maxZoomLevel = 4;
			this.minZoomLevel = 1;
			
			// add mouse listeners for zoombox drawing
			this.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseDragged(MouseEvent e) {
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					ImageViewer.this.repaint();
				}
				
			});
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
					ImageViewer.this.repaint();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					Point mousePos = e.getPoint();
					int zoomBoxWidth = (int)(ImageViewer.this.getWidth() 
							* ImageViewer.this.zoomBoxFraction);
					int zoomBoxHeight = (int)(ImageViewer.this.getHeight() 
							* ImageViewer.this.zoomBoxFraction);
					
					// if clicked
					if(e.getButton() == MouseEvent.BUTTON1 
							&& ImageViewer.this.zoomLevel < ImageViewer.this.maxZoomLevel) {
						ImageViewer.this.imageXOffset = screenXToImageX(mousePos.x - (zoomBoxWidth / 2));
						ImageViewer.this.imageYOffset = screenYToImageY(mousePos.y - (zoomBoxHeight / 2));
						ImageViewer.this.zoomLevel /= ImageViewer.this.zoomBoxFraction;
					} else if(e.getButton() == MouseEvent.BUTTON3 &&
							ImageViewer.this.zoomLevel > ImageViewer.this.minZoomLevel){
						ImageViewer.this.zoomLevel *= ImageViewer.this.zoomBoxFraction;
						
						// if zoomed out all the way, return the image to screen center
						if(ImageViewer.this.zoomLevel == 1) {
							ImageViewer.this.imageXOffset = 0;
							ImageViewer.this.imageYOffset = 0;
						}
					}
					
					// check for out of bounds, reset to default pos.
					if(ImageViewer.this.imageXOffset < 0)
						ImageViewer.this.imageXOffset = 0;
					if(ImageViewer.this.imageYOffset < 0)
						ImageViewer.this.imageYOffset = 0;
					
					// repaint with new coordinates
					ImageViewer.this.repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}
				
			});
		}
		
		/**
		 * Sets the maximum amount a photo can be zoomed in.
		 * @param maxZoomLevel The maximum times a photo can be
		 * magnified. Default is 4X.
		 */
		public void setMaximumZoomLevel(float maxZoomLevel) {
			if(this.maxZoomLevel > 1)
				this.maxZoomLevel = maxZoomLevel;
			else
				throw new IllegalArgumentException();
		}
		
		/**
		 * Gets the maximum amount a photo can be zoomed in.
		 * @return The max number of times magnification.
		 */
		public float getMaximumZoomLevel() {
			return this.maxZoomLevel;
		}
		
		/**
		 * Sets the farthest out a photo can be zoomed.
		 * @param minZoomLevel The smallest number of times
		 * magnification for the control. 1 is normal image
		 * size. Greater than one is magnified, and less than
		 * one is smaller than actual image size.
		 */
		public void setMinimumZoomLevel(float minZoomLevel) {
			if(this.minZoomLevel <= 1)
				this.minZoomLevel = minZoomLevel;
			else
				throw new IllegalArgumentException();
		}
		
		/**
		 * Gets the minimum times magnification for the photo.
		 * @return The smallest fraction of its original size
		 * the photo can be displayed at. 1 is 100%.
		 */
		public float getMinimumZoomLevel() {
			return this.minZoomLevel;
		}
		
		/**
		 * Sets the current zoom level at the current screen offset.
		 * @param zoomLevel The number of times magnification to
		 * apply to the image.
		 */
		public void setZoomLevel(float zoomLevel) {
			if(zoomLevel >= this.minZoomLevel && zoomLevel <= this.maxZoomLevel) {
				this.zoomLevel = zoomLevel;
				this.repaint();
			} else
				throw new IllegalArgumentException();
		}
		
		/**
		 * Gets the current number of times magnification of the image.
		 * @return The number of times the image has been magnified.
		 */
		public float getZoomLevel() {
			return this.zoomLevel;
		}
		
		/**
		 * Sets the size of the zoom box as a fraction of the width
		 * of the image.
		 * @param zoomBoxFraction The width of the zoom box.
		 */
		public void setZoomboxFraction(float zoomBoxFraction) {
			if(zoomBoxFraction > 0 && zoomBoxFraction < 1)
				this.zoomBoxFraction = zoomBoxFraction;
			else
				throw new IllegalArgumentException();
		}
		
		/**
		 * Gets the width of the zoombox as a fraction of the width
		 * of the image.
		 * @return Zoombox size as fraction of the image size.
		 */
		public float getZoomboxFraction() {
			return this.zoomBoxFraction;
		}
		
		/**
		 * Sets the color of the text displayed in the zoombox
		 * "Click to zoom" text.
		 * @param zoomboxTextColor The color for the zoombox text.
		 */
		public void setZoomboxTextColor(Color zoomboxTextColor) {
			this.zoomBoxTextColor = zoomboxTextColor;
		}
		
		/**
		 * Gets the color of the text drawn in the zoom box.
		 * @return Text color of the zoombox.
		 */
		public Color getZoomboxTextColor() {
			return this.zoomBoxTextColor;
		}
		
		/**
		 * Converts coordinates in the ImageViewer to coordinates on the
		 * image.
		 * @param screenX An X coordinate on the ImageViewer.
		 * @return The converted coordinate.
		 */
		private int screenXToImageX(int screenX) {
			return (int)((float)this.backgroundImage.getWidth(null) / this.getWidth() * screenX);
		}
		
		/**
		 * Converts coordinates in the ImageViewer to coordinates on the
		 * image.
		 * @param screenY A Y coordinate on the ImageViewer.
		 * @return The converted coordinate.
		 */
		private int screenYToImageY(int screenY) {
			return (int)((float)this.backgroundImage.getHeight(null) / this.getHeight() * screenY);
		}
		
		/**
		 * Converts coordinates on the image to coordinates on the
		 * ImageViewer.
		 * @param imageX An X coordinate on the image itself.
		 * @return The converted coordinate.
		 */
		private int imageXToScreenX(int imageX) {
			return (int)((float)this.backgroundImage.getWidth(null) 
					/ this.backgroundImage.getHeight(null) * this.getHeight());
		}
		
		/**
		 * Converts coordinates on the screen to coordinates on the
		 * ImageViewer.
		 * @param imageY A Y coordinate on the image itself.
		 * @return The converted coordinate.
		 */
		private int imageYToScreenY(int imageY) {
			return (int)((float)this.backgroundImage.getHeight(null) 
					/ this.backgroundImage.getWidth(null) * this.getWidth());
		}
		
		/**
		 * Called by the system, this function repaints the panel
		 * whenever part of it is invalidated. There is no reason
		 * for the user to call this. Use repaint() instead, if you
		 * wish to update the panel's paint.
		 */
		@Override
		public void paintComponent(Graphics g) {
			
			// paint background image
			g.setColor(this.getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if(this.backgroundImage != null) {
				g.drawImage(this.backgroundImage, 0, 0, 
						this.getWidth(), this.getHeight(), this.imageXOffset, this.imageYOffset, 
						(int)((this.backgroundImage.getWidth(null) - this.imageXOffset) / this.zoomLevel),
						(int)((this.backgroundImage.getHeight(null) - this.imageXOffset) / this.zoomLevel), null);
			}
			
			// paint zoom square
			Point mousePos;
			if((mousePos = this.getMousePosition()) != null) {
				int zoomBoxWidth = (int)(this.getWidth() * this.zoomBoxFraction);
				int zoomBoxHeight = (int)(this.getHeight() * this.zoomBoxFraction);
				int strLen1 = g.getFontMetrics().stringWidth("Left Click to Zoom in");
				int strLen2 = g.getFontMetrics().stringWidth("Right Click to Zoom out");
						
				g.drawImage(UIBuilder.this.transparency, mousePos.x - (zoomBoxWidth / 2), mousePos.y - (zoomBoxHeight / 2), 
						mousePos.x + zoomBoxWidth - (zoomBoxWidth / 2), mousePos.y + zoomBoxHeight - (zoomBoxHeight / 2), 0, 0, 
						UIBuilder.this.transparency.getWidth(null),
						UIBuilder.this.transparency.getHeight(null), null);
						
				g.setColor(this.zoomBoxTextColor);
				g.setFont(g.getFont().deriveFont(Font.BOLD, 14));
				g.drawString("Left Click to Zoom in", mousePos.x - (strLen1 / 2), 
						mousePos.y);
				g.drawString("Right Click to Zoom out", mousePos.x - (strLen2 / 2), 
						mousePos.y + 20);
			}
			
		}
	}
	
	/**
	 * A generic modal dialog class that allows for the setting of background 
	 * images, and the loading of some default properties from the UIBuilder
	 * template (icon, background image, etc). Display the window with
	 * Window.setVisible(true);
	 */
	@SuppressWarnings("serial")
	public class Window extends JDialog {
		/** The panel that serves as ContentPane and image canvas */
		private BackgroundPanel panel;
		
		/**
		 * An overloaded constructor that allows for creation of a dialog that
		 * is modal to the given parent Window and has the specified title.
		 * The window will be made at arbitrary cooridinates and will be of a 
		 * default size of 600, 500.
		 * @param parent Window that spawned this one. This window will be 
		 * modal towards its parent.
		 * @param title Title for the Window title bar.
		 */
		public Window(java.awt.Window parent, String title) {
			this(parent, title, 600, 500);
		}
		
		/**
		 * An overloaded constructor that creates a dialog that is modal to the
		 * specified parent Window and has the specified title. 
		 * @param parent The Window to be modal towards whilst this one is open.
		 * @param title The title for this Window's title bar.
		 * @param width The width to make this window.
		 * @param height The height to make this window.
		 */
		public Window(java.awt.Window parent, String title, int width, int height) {
			this(parent, title, 150, 150, width, height);
		}
		
		/**
		 * The biggest and baddest of the Window constructors, creates a modal 
		 * dialog/"Window that has the specfied parameters.
		 * @param parent Window that this one will be modal over.
		 * @param title The text in the Window's title bar.
		 * @param x The x coordinate of the Window's location.
		 * @param y The y coordinate of the Window's location.
		 * @param width The width of the Window.
		 * @param height The height to make the Window.
		 */
		public Window(java.awt.Window parent, String title, int x, int y, int width, int height) {
			super(parent, title);
			this.setModal(true);
			this.setLayout(null);
			this.setIconImage(UIBuilder.this.iconImage);
			this.setLocation(x, y);
			this.setSize(width, height);
			this.setResizable(false);
			this.panel = new BackgroundPanel(UIBuilder.this.backgroundImage);
			this.setContentPane(this.panel);
		}
		
		/**
		 * Sets the background Image for the Window by setting
		 * the background of the resident BackgroundPanel ContentPane.
		 * @param backgroundImage The new background Image for the 
		 * Window.
		 */
		public void setBackgroundImage(Image backgroundImage) {
			this.panel.setBackgroundImage(backgroundImage);
		}
		
		/**
		 * Gets the current background Image painted to this 
		 * Window.
		 * @return The Image being painted to this Window's 
		 * background.
		 */
		public Image getBackgroundImage() {
			return this.panel.getBackgroundImage();
		}
		
		/**
		 * Overrides default functionality of JDialog.add()
		 * to add controls, instead, to the BackgroundPanel,
		 * maintaining proper painting across the screen,
		 * while still keeping the Components visible.
		 * @param comp The Component to add to the ContentPane
		 * panel of this Window.
		 */
		@Override
		public Component add(Component comp) {
			return this.panel.add(comp);
		}
		
		/**
		 * Sets the background color of this window, if there 
		 * is no Image set to paint, this color will be painted
		 * to the backdrop instead.
		 */
		@Override
		public void setBackground(Color c) {
			if(this.panel != null)
				this.panel.setBackground(c);
		}
	}
	
	/**
	 * A textured button class, meant to extend and replace the standard
	 * JButton Swing facilities. By default, receives texture from theme
	 * loaded with construction of a UIBuilder object, unifying application
	 * under a common look.
	 */
	@SuppressWarnings("serial")
	public class Button extends JButton {
		/** If true, swaps standard texture for big button texture */
		private boolean bigButton;
		
		/**
		 * Creates a standard button of standard size at
		 * coordinates 0, 0.
		 * @param title The text for the button.
		 */
		public Button(String title) {
			this(title, 0, 0, 150, 25);
		}
		
		/**
		 * Creates a standard button of standard size at
		 * the specified coordinates
		 * @param title The text for the button.
		 * @param x The x coordinate for the button.
		 * @param y The y coordinates for the button.
		 */
		public Button(String title, int x, int y) {
			this(title, 0, 0, 150, 25);
		}
		
		/**
		 * All inclusive constructor, creates a button with the standard
		 * UIBuilder themed button texture.
		 * @param title The text that goes on the button.
		 * @param x The x coordinate for the location of the button.
		 * @param y The y coordinate.
		 * @param width The width of the button.
		 * @param height The height of the button.
		 */
		public Button(String title, int x, int y, int width, int height) {
			super(title);
			this.setLocation(x, y);
			this.setSize(width, height);
			this.setForeground(UIBuilder.this.defaultForeground);
			this.setOpaque(false);
			this.bigButton = false;
			
			// register repaint listener
			this.getModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Button.this.repaint();
				}
			});
		}
		
		/**
		 * Sets the bigButton property. Bigbutton causes button to be 
		 * repainted with a higher resolution texture.
		 * @param bigButton True enables bigButton, false disables.
		 */
		public void setBigButton(boolean bigButton) {
			this.bigButton = bigButton;
		}
		
		/**
		 * A System method, this is overrided to allow the painting of the
		 * button's custom texture. 
		 * @param g The graphics context, belonging to the window, to paint with.
		 */
		@Override
		public void paint(Graphics g) {
			
			// paint specified background, in case image is transparent
			if(this.isOpaque()) {
				g.setColor(this.getBackground());
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
			
			// paint button images
			Image image = this.bigButton ? 
					UIBuilder.this.bigButtonImage : UIBuilder.this.buttonImage;
			
			if (this.getModel().isPressed()) {
				g.drawImage(image, 0, 0, 
						this.getWidth(), this.getHeight(), 0, 
						(image.getHeight(null) / 3) * 2, 
						image.getWidth(null),
						image.getHeight(null), null);
			} else if(this.getModel().isRollover()) {
				g.drawImage(image, 0, 0, 
						this.getWidth(), this.getHeight(), 0, 
						image.getHeight(null) / 3, 
						image.getWidth(null),
						(image.getHeight(null) / 3) * 2, null);
			} else {
				g.drawImage(image, 0, 0, 
					this.getWidth(), this.getHeight(), 0, 0, 
						image.getWidth(null),
						image.getHeight(null) / 3, null);
			}
			
			// paint button text
			g.setFont(this.getFont());
			g.setColor(this.getForeground());
			int textWidth = g.getFontMetrics().stringWidth(this.getText());
			int textHeight = g.getFontMetrics().getHeight();
			g.drawString(this.getText(), (this.getWidth() / 2) - (textWidth / 2),
					(int)((this.getHeight() / 2) + (textHeight / 2.0) - 3));
		}
	}
	
	/**
	 * A themed text box replacement for JTextField.
	 */
	@SuppressWarnings("serial")
	public class TextBox extends JPasswordField {
		
		/**
		 * Creates a TextBox of the given dimensions.
		 * @param width Width for the new text box.
		 * @param height Height for the new text box
		 */
		public TextBox(int width, int height) {
			this(0, 0, width, height);
		}
		
		/**
		 * Creates a new themed text box of exactly the coordinates and size
		 * specified.
		 * @param x The x coordinate to draw text box at.
		 * @param y The y coordinate to draw text box at.
		 * @param width The width for the new text box.
		 * @param height The height for the box.
		 */
		public TextBox(int x, int y, int width, int height) {
			this("", x, y, width, height);
		}
		
		/**
		 * All in one constructor that sets dimensions, location, and text
		 * of new TextBox and builds it in one step.
		 * @param text The text to place in the text box.
		 * @param x The x coordinate to draw text box at.
		 * @param y The y coordinate to draw text box at.
		 * @param width The width for the new text box.
		 * @param height The height for the box.
		 */
		public TextBox(String text, int x, int y, int width, int height) {
			super(text);
			
			super.setMargin(new Insets(5, 10, 5, 10));
			this.setLocation(x, y);
			this.setSize(width, height);
			super.setEchoChar((char)0); // disable password field
		}
		
		/**
		 * Determines whether this TextBox has the password mask enabled.
		 * Returns true if the password text will be masked by '*' and false
		 * if the password text is visible.
		 * @return Returns whether or not password mask is enabled.
		 */
		public boolean isPasswordMaskEnabled() {
			return super.getEchoChar() == '*';
		}
		
		/**
		 * Enables or disables the password mask of '*'.
		 * @param enabled If true, enables password mask. If false, the mask 
		 * will be disabled.
		 */
		public void setPasswordMaskEnabled(boolean enabled) {
			if(enabled)
				super.setEchoChar('*'); // enable mask
			else
				super.setEchoChar((char)0); // disable mask
		}
		
		/**
		 * Overrides the superclass setEchoChar method to prevent it from being
		 * used, interfering with the function of the setPasswordMaskEnabled 
		 * method.
		 * @param echoChar Does nothing.
		 */
		@Override
		@Deprecated
		public void setEchoChar(char echoChar) {
			// do nothing, prevent use
		}
		
		/**
		 * Gets the text in this TextBox.
		 * @return The contents of this text box.
		 */
		@Override
		public String getText() {
			return new String(super.getPassword());
		}
		
		/**
		 * Removed. This function is has an Override to prevent
		 * margins from being tweaked, messing up the drawing of the
		 * frame around the text box.
		 */
		@Override
		@Deprecated
		public void setMargin(Insets m) {
			// do nothing, but prevent use
		}
		
		/**
		 * Draws custom texturing in the TextBox. Called by Swing
		 * system.
		 */
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(UIBuilder.this.textBoxImage, 0, 0, 
					this.getWidth(), this.getHeight(), 0, 0, 
					UIBuilder.this.textBoxImage.getWidth(null),
					UIBuilder.this.textBoxImage.getHeight(null), null);
		}
	}
	
	/**
	 * A custom Label class, replacement for JLabel, that
	 * allows for the easy production of Labels that follow an
	 * already built template in UIBuilder, and that support 
	 * text shadows.
	 */
	@SuppressWarnings("serial")
	public class Label extends JLabel {
		/** Is the shadow enabled for this Label */
		private boolean shadowEnabled;
		/** What is the color for this shadow */
		private Color shadowColor;
		/** How many pixels will the shadow be from the text */
		private int shadowOffset;
		
		/**
		 * Creates a label at 0,0 with the specified text.
		 * @param text Label text.
		 */
		public Label(String text) {
			this(text, 0, 0, 150, 25);
		}
		
		/**
		 * Creates a label with the specified text at the given coordinates
		 * of the specified dimensions.
		 * @param text The text to be displayed on the label.
		 * @param x The x coordinate of the position.
		 * @param y The y coordinate.
		 * @param width The width for the new text box.
		 * @param height The height.
		 */
		public Label(String text, int x, int y, int width, int height) {
			super(text);
			this.setOpaque(false);
			this.setSize(width, height);
			this.setLocation(x, y);
			this.setForeground(UIBuilder.this.defaultForeground);
			this.shadowEnabled = UIBuilder.this.labelShadowEnabled;
			this.shadowColor = UIBuilder.this.labelShadowColor;
			this.shadowOffset = UIBuilder.this.labelShadowOffset;
		}
		
		/**
		 * Enable text shadowing.
		 * @param shadowEnabled True if shadow enabled, false if shadow disabled.
		 */
		public void setShadowEnabled(boolean shadowEnabled) {
			this.shadowEnabled = true;
			this.repaint();
		}
		
		/**
		 * Check if text shadow is enabled.
		 * @return True if shadow enabled, false if shadow disabled.
		 */
		public boolean isShadowEnabled() {
			return this.shadowEnabled;
		}
		
		/**
		 * Sets the color for the text shadow.
		 * @param shadowColor Text shadow color.
		 */
		public void setShadowColor(Color shadowColor) {
			this.shadowColor = shadowColor;
			this.repaint();
		}
		
		/**
		 * Gets the current color of the Text Shadow.
		 * @return The color of the text shadow.
		 */
		public Color getShadowColor() {
			return this.shadowColor;
		}
		
		/**
		 * Sets how many pixels diagonal the shadow is from the text.
		 * @param shadowOffset How many pixels.
		 */
		public void setShadowOffset(int shadowOffset) {
			this.shadowOffset = shadowOffset;
			this.repaint();
		}
		
		/**
		 * Gets how many pixels diagonal that the shadow is
		 * offset from the text.
		 * @return How far from text shadow is.
		 */
		public int getShadowOffset() {
			return this.shadowOffset;
		}
		
		/**
		 * Overrides paint function, to allow label to custom
		 * paint its text.
		 */
		@Override
		public void paint(Graphics g) {
			// draw text shadow
			if(this.shadowEnabled) {
				g.setColor(this.shadowColor);
				g.drawString(this.getText(), 0 + this.shadowOffset, 
						(this.getHeight() / 2) + this.shadowOffset);
			}
			
			//draw text
			g.setColor(this.getForeground());
			g.drawString(this.getText(), 0, this.getHeight() / 2);
		}
	}
	
	/**
	 * Replaces JList. No themes or additional functionality yet.
	 */
	@SuppressWarnings("serial")
	public class List extends JList {
		
		/**
		 * Creates a List.
		 * @param width Width of list
		 * @param height Height of new list.
		 */
		public List(int width, int height) {
			this(0, 0, width, height);
		}
		
		/**
		 * Creates list of specified dimensions at given coordinates.
		 * @param x x coordinate to place list.
		 * @param y y coordinate.
		 * @param width Width for new list box.
		 * @param height Height for new list box.
		 */
		public List(int x, int y, int width, int height) {
			super();
			//this.setLocation(x, y);
			//this.setSize(width, height);
			DefaultListModel model = new DefaultListModel();
			this.setModel(model);
		}

		/**
		 * Adds an item to the list at the specified index.
		 * @param index Index to add item.
		 * @param element Item to add to list.
		 */
		public void add(int index, Object element) {
			getDefaultListModel().add(index, element);
		}
		
		/**
		 * Adds item to end of the list.
		 * @param element Item to add to the end of the list.
		 */
		public void add(Object element) {
			getDefaultListModel().addElement(element);
		}
		
		/**
		 * Returns the model that controls this list. Use to get more in depth
		 * control of the list facilities.
		 * @return ListModel controlling this list.
		 */
		public DefaultListModel getDefaultListModel() {
			return (DefaultListModel)this.getModel();
		}
		
		/**
		 * Does nothing yet. Will hopefully eventually paint custom themes.
		 */
		@Override
		public void paint(Graphics g) {
			super.paint(g);
		/*	// draw left border
			g.drawImage(UIBuilder.this.vbarImage, 0, 0, 
					5, this.getHeight(), 0, 0, 
					5,
					UIBuilder.this.vbarImage.getHeight(null), null);
			
			// draw right border
			g.drawImage(UIBuilder.this.vbarImage, this.getWidth() - 5, 0, 
					this.getWidth(), this.getHeight(), 0, 0, 
					5,
					UIBuilder.this.vbarImage.getHeight(null), null);
			
			// draw top border
			g.drawImage(UIBuilder.this.hbarImage, 0, 0, 
					this.getWidth(), 5, 0, 0, 
					UIBuilder.this.hbarImage.getWidth(null),
					5, null);
			// draw bottom border
			g.drawImage(UIBuilder.this.hbarImage, 0, this.getHeight() - 5, 
					this.getWidth(), this.getHeight(), 0, 0, 
					UIBuilder.this.hbarImage.getWidth(null),
					5, null);*/
		}
	}
	
	/**
	 * Replaces JCheckbox with a themed, convenient check box.
	 */
	@SuppressWarnings("serial")
	public class CheckBox extends JCheckBox {
		
		/**
		 * Creates a new CheckBox object with a caption.
		 * @param text The Label for the new CheckBox.
		 */
		public CheckBox(String text) {
			this(text, 150, 25);
		}
		
		/**
		 * Creates a new CheckBox object with a caption of specified dimensions.
		 * @param text The Label for the new CheckBox.
		 * @param width The width for the new CheckBox.
		 * @param height The height.
		 */
		public CheckBox(String text, int width, int height) {
			this(text, 0, 0, width, height);
		}
		
		/**
		 * Creates a new CheckBox object with a caption of specified dimensions
		 * at given coordinates.
		 * @param text Caption for the check box.
		 * @param x x coordinate for location.
		 * @param y y coordinate.
		 * @param width Width for the new check box control.
		 * @param height Height for the new check box control.
		 */
		public CheckBox(String text, int x, int y, int width, int height) {
			super(text); // initialize super class
			
			// set size and location
			this.setSize(width, height);
			this.setLocation(x, y);
			
			// set font color
			this.setForeground(UIBuilder.this.defaultForeground);
			this.setOpaque(false);
			
			// set textures
			BufferedImage image = (BufferedImage) UIBuilder.this.checkboxImage;
			image.getSubimage(0, 0, image.getWidth(), image.getHeight() / 3);
			this.setIcon(new ImageIcon(image.getSubimage(0, 0, image.getWidth(), image.getHeight() / 3)));
			this.setSelectedIcon(new ImageIcon(image.getSubimage(0, (image.getHeight() / 3) * 2, image.getWidth(), image.getHeight() / 3)));
			this.setPressedIcon(new ImageIcon(image.getSubimage(0, image.getHeight() / 3, image.getWidth(), image.getHeight() / 3)));
		}
	}
	
	/**
	 * ImageShuffler horizontally scrolling miniature image viewer. Good for
	 * browsing through several images at a time, or presenting several options
	 * to the user simultaneously.
	 */
	@SuppressWarnings("serial")
	public class ImageShuffler extends JPanel {
		/** Color of the text shadow */
		private Color shadowColor;
		/** Enables shadows on text */
		private boolean enableShadow;
		/** How many pixels offset text shadows should be */
		private int shadowOffset;
		/** Image to display in the background of the control. */
		private Image backgroundImage;
		/** Array of images to scroll through in control */
		private Image[] imageArray;
		/** Array of captions corresponding to the array of images */
		private String[] captionArray;
		/** Text displayed when the control is void of images */
		private String emptyText;
		/** Swing Timer that updates scrolling as mouse hovers */
		private Timer scrollTimer;
		/** Callback function for when an ImageIcon is clicked */
		private IconClickedListener clickListener;
		/** Margins around each image from the edge of its cell */
		private Insets margins;
		/** Margins around each image when mouse is hovering over it */
		private Insets hoverMargins;
		/** How many pixels the control is scrolled horizontally */
		private int scrollOffset;
		/** How many pixels to make each cell containing a single image */
		private int cellWidth;
		
		/**
		 * Constructs a horizontally scrolling image viewer that scrolls when 
		 * the mouse gets new the respective edges.
		 * @param imageArray An array of images to display and scroll through.
		 * @param captionArray An array of captions for each of the images.
		 * @param x The x coordinate for the control.
		 * @param y The y coordinate for the control.
		 * @param width The width for the control.
		 * @param height The height for the control.
		 */
		public ImageShuffler(Image[] imageArray, String[] captionArray, int x, int y, int width, int height) {
			super();
			
			// store constructor parameters
			this.imageArray = imageArray;
			this.captionArray = captionArray;
			this.setLocation(x, y);
			this.setSize(width, height);
			this.setOpaque(false);
			this.setForeground(UIBuilder.this.defaultForeground);
			this.backgroundImage = null;
			this.clickListener = null;
			this.cellWidth = width / 4;
			this.margins = new Insets(20, 20, 20 , 20);
			this.hoverMargins = new Insets(10, 10, 10 , 10);
			this.scrollOffset = 0;
			this.shadowColor = UIBuilder.this.shufflerShadowColor;
			this.enableShadow = UIBuilder.this.shufflerShadowEnabled;
			this.shadowOffset = UIBuilder.this.shufflerShadowOffset;
			this.setFont(this.getFont().deriveFont(Font.BOLD));
			this.emptyText = "No Images";
			
			// set to repaint every time that the mouse is moved
			this.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent e) {
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					ImageShuffler.this.repaint();
				}
			});
			
			// setup scroll timer, repaint routines and click handlers
			this.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(ImageShuffler.this.clickListener != null && ImageShuffler.this.imageArray != null) {
						int index = (e.getPoint().x + ImageShuffler.this.scrollOffset) / ImageShuffler.this.cellWidth;
						if(index < ImageShuffler.this.imageArray.length)
							ImageShuffler.this.clickListener.iconClicked(index, null);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					if(ImageShuffler.this.imageArray != null)
						scrollTimer.start();
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					ImageShuffler.this.repaint();
					scrollTimer.stop();
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}
			});
			
			// setup scroll timer. will be activated on mouse enter
			scrollTimer = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Point mousePos = ImageShuffler.this.getMousePosition();
					if(mousePos != null) {
						if(ImageShuffler.this.scrollOffset < 
								(ImageShuffler.this.cellWidth * 
										ImageShuffler.this.imageArray.length) 
										- ImageShuffler.this.getWidth()
								&& Math.abs(mousePos.x - ImageShuffler.this.getWidth()) < 40) {
							ImageShuffler.this.scrollOffset += 20;
						} else if(ImageShuffler.this.scrollOffset > 0 
								&& mousePos.x < 40) {
							ImageShuffler.this.scrollOffset -= 20;
						}
					}
					ImageShuffler.this.repaint();
				}
			});
		}
		
		/**
		 * Sets the margins around each image in the control.
		 * @param margins Default margins are 20px on all four sides.
		 */
		public void setMargins(Insets margins) {
			this.margins = margins;
		}
		
		/**
		 * Gets the margins currently employed for spacing out each 
		 * individual image.
		 * @return An Insets containing the current Margins.
		 */
		public Insets getMargins() {
			return this.margins;
		}
		
		/**
		 * Sets the margins around each icon that has the mouse hovering
		 * over it. A sort of way to "highlight" the hovered icon.
		 * @param hoverMargins Larger margins, highlighting the selected
		 * icon.
		 */
		public void setHoverMargins(Insets hoverMargins) {
			this.hoverMargins = hoverMargins;
		}
		
		/**
		 * Get margins used to draw icons while the mouse is hovering over them.
		 * @return
		 */
		public Insets getHoverMargins() {
			return this.hoverMargins;
		}
		
		/**
		 * Sets the number of pixels wide each Image Icon cell is.
		 * @param cellWidth The width of the image cell.
		 */
		public void setCellWidth(int cellWidth) {
			this.cellWidth = cellWidth;
		}
		
		/**
		 * Gets the number of pixels wide each cell is.
		 * @return Number of pixels wide each Image cell is.
		 */
		public int getCellWidth() {
			return this.cellWidth;
		}
		
		/**
		 * Sets a callback function that will be called when an icon is clicked.
		 * @param clickListener An IconClickedListener interface that does fancy
		 * stuff!
		 */
		public void setIconClickedListener(IconClickedListener clickListener) {
			this.clickListener = clickListener;
		}
		
		/**
		 * Sets the Image to be displayed in the background of this control.
		 * @param backgroundImage The image to display, or null for none.
		 */
		public void setBackgroundImage(Image backgroundImage) {
			this.backgroundImage = backgroundImage;
		}
		
		/**
		 * Gets the image currently on display in the background of this 
		 * control.
		 * @return The image in the background of this control.
		 */
		public Image getBackgroundImage() {
			return this.backgroundImage;
		}
		
		/**
		 * Scrolls the control to the leftmost position.
		 */
		public void scrollToBeginning() {
			this.scrollOffset = 0;
		}
		
		/**
		 * Sets the array of images drawn in this control.
		 * @param imageArray New array of images to draw in this
		 * control.
		 */
		public void setImageArray(Image[] imageArray) {
			this.imageArray = imageArray;
			this.repaint();
		}
		
		/**
		 * Gets array of images currently being drawn in this control's icon's.
		 * @return The array of images drawn to this control.
		 */
		public Image[] getImageArray() {
			return this.imageArray;
		}
		
		/**
		 * Sets caption displayed when there are no images in imageArray.
		 * @param emptyText Text displayed when empty.
		 */
		public void setEmptyText(String emptyText) {
			this.emptyText = emptyText;
		}
		
		/**
		 * Gets text displayed when empty.
		 * @return Text displayed when there are no images in control.
		 */
		public String getEmptyText() {
			return this.emptyText;
		}
		
		/**
		 * Paints the ImageShuffler Control. Called by Swing system.
		 * @param g The Graphics context to paint with.
		 */
		@Override
		public void paint(Graphics g) {
			Point mousePos = this.getMousePosition();
			g.setColor(Color.BLACK);
			if(this.backgroundImage != null) {
				g.drawImage(this.backgroundImage, 0, 0, 
						this.getWidth(), 
						this.getHeight(), 
						0, 0, this.backgroundImage.getWidth(null), 
						this.backgroundImage.getHeight(null), null);
			}
			
			// paint each image in array to control
			if(this.imageArray == null || this.imageArray.length == 0) {
				int x = (this.getWidth() / 2) - 
						(g.getFontMetrics().stringWidth(this.emptyText) / 2);
				int y = this.getHeight() / 2;
				
				// draw shadow
				g.setColor(this.shadowColor);
				g.drawString(this.emptyText, x + this.shadowOffset,
						y + this.shadowOffset);
				
				// draw text
				g.setColor(this.getForeground());
				g.drawString(this.emptyText, x, y);
				
			} else {
				for(int i = 0; i < this.imageArray.length; i++) {
	
					if(this.imageArray[i] != null) {
						Rectangle r = new Rectangle((this.cellWidth * i) 
								+ this.margins.left - this.scrollOffset, 
								this.margins.top, this.cellWidth - this.margins.right, 
								this.getHeight() - this.margins.top - this.margins.bottom);
						
						// if mouse hover, expand image to "hover margin" size
						if(mousePos != null && r.contains(mousePos)) {
							r.x -= this.hoverMargins.left;
							r.y -= this.hoverMargins.top;
							r.width += this.hoverMargins.right;
							r.height += this.hoverMargins.top + this.hoverMargins.bottom;
						}
						
						// draw current thumb nail
						g.drawImage(this.imageArray[i], r.x, r.y, 
								r.x + r.width, 
								r.y + r.height, 
								0, 0, this.imageArray[i].getWidth(null), 
								this.imageArray[i].getHeight(null), null);
						
						// draw thumb nail caption
						if(this.captionArray != null && this.captionArray[i] != null) {
							int strLen = g.getFontMetrics().stringWidth(this.captionArray[i]);
							g.setFont(this.getFont());
							if(this.enableShadow) {
								g.setColor(this.shadowColor);
								g.drawString(this.captionArray[i], r.x + (this.cellWidth / 2 ) -
										(strLen / 2) - 10 + this.shadowOffset,
										this.getHeight() - g.getFontMetrics().getHeight() + this.shadowOffset);
							}
							
							g.setColor(this.getForeground());
							g.drawString(this.captionArray[i], r.x + (this.cellWidth / 2 ) -
									(strLen / 2) - 10,
									this.getHeight() - g.getFontMetrics().getHeight());
						}
					}
				}
			}
			
			// paint scroll arrows
			if(mousePos != null) {
				g.drawImage(UIBuilder.this.leftArrow, 0, 0, 
						UIBuilder.this.leftArrow.getWidth(null), 
						this.getHeight(), 
						0, 0, UIBuilder.this.leftArrow.getWidth(null), 
						UIBuilder.this.leftArrow.getHeight(null), null);
				g.drawImage(UIBuilder.this.rightArrow, this.getWidth() 
						- UIBuilder.this.leftArrow.getWidth(null), 0, 
						this.getWidth(), 
						this.getHeight(), 
						0, 0, UIBuilder.this.rightArrow.getWidth(null), 
						UIBuilder.this.rightArrow.getHeight(null), null);
			}
		}
		
		
	}
	
	/**
	 * Register with ImageShuffler to be notified every time an icon is 
	 * clicked.
	 */
	public interface IconClickedListener {
		public void iconClicked(int index, String iconCaption);
	}
}
