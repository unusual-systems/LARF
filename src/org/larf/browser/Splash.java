package org.larf.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * A splash screen suitable for displaying as a program loads. Two
 * uses are provided:
 * <p>
 * 1) Pass in the filename to the constructor of an image file.  The
 * splash screen will be created & displayed from that.
 * <p>
 * 2) Override the null constructor to assemble your own components
 * <p>
 * The splash is displayed as part of the constructor; to remove it
 * when the application has started, call the dispose() method.
 * <p>
 * The GIF load may be quicker than the assembler, as less classes
 * need to be loaded initially, but there does not appear to be a
 * significant difference between them.
 * <p>
 * NB - This class is growing as more features are added.  Given that
 * splash screens ought to be small and quick, you might want to copy
 * it to your directory and delete all the stuff you don't need.
 * <p>
 * Timestamp provided for a convenient way of timing start-up
 * @author M Hill
 */

public class Splash extends JWindow
{

	/**
	 * Construct and display splash screen from image given by name, at
	 * ./images/ subdirectory of the package this class is in
	 */
	public Splash(String imageName)
	{
		this(new File("./images/"+imageName+".gif"));
	}

	/**
	 * Construct & display splash screen from image at given file location
	 */
	public Splash(File file)
	{
		this(Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath()));
	}

	/**
	 * Construct & display splash screen from image at given url
	 */
	public Splash(URL url)
	{
		this(Toolkit.getDefaultToolkit().createImage(url));
	}
	
	
	/** Construct & show splash screen from given image
	 */
	public Splash(Image splashImage)
	{
		getContentPane().add(new JLabel(new ImageIcon(splashImage)));
		showCenter();
	}

	/** Null constructor for descendents that want to construct their own
	 */
	public Splash()
	{}

	/** General purpose constructor for instant splash-screens */
	public Splash(String title, String subtitle, String version,
								  Color bg, Color fg,
								  Image coyLogo, String coyName)
	{
		makeSplash(title, subtitle, version, bg, fg, coyLogo, coyName);
		showCenter();
	}

	public void makeSplash(String title, String subtitle, String version,
								  Color bg, Color fg,
								  Image coyLogo, String coyName)
	{
		Container contentPane = getContentPane();
		contentPane.setBackground(bg);
		JPanel newContent = new JPanel(new BorderLayout());
		newContent.setBackground(fg); //for bevel border
		newContent.setBorder(BorderFactory.createCompoundBorder(
										BorderFactory.createRaisedBevelBorder(),
										BorderFactory.createLineBorder(bg, 10)
									));
		contentPane.add(newContent);
		contentPane = newContent;
		
		//assemble top panel - title
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(bg);
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(Font.decode("dialog").deriveFont(24F).deriveFont(Font.BOLD));   //dialog font, 24pt bold
		titleLabel.setForeground(fg);
		topPanel.add(titleLabel, BorderLayout.WEST);
		if (subtitle != null)
		{
			JLabel subTitleLabel = new JLabel(subtitle);
			subTitleLabel.setFont(titleLabel.getFont().deriveFont(16F));
			subTitleLabel.setForeground(fg);
			topPanel.add(subTitleLabel, BorderLayout.SOUTH);
		}
		contentPane.add(topPanel, BorderLayout.NORTH);
		
		//assemble middle panel
//    JPanel middlePanel = new JPanel(new FlowLayout());
//    middlePanel.setBackground(Color.black);
//    JLabel text;
//    text = new JLabel("Application to design and display spacecraft");
//    text.setForeground(Color.yellow);
//    middlePanel.add(text);
//    text = new JLabel("monitoring systems");
//    text.setForeground(Color.yellow);
//    middlePanel.add(text);
//    contentPane.add(middlePanel, BorderLayout.EAST);
		
		//assemble bottom panel (with company logo)
		JLabel coyLabel = new JLabel();
		coyLabel.setFont(titleLabel.getFont().deriveFont(12F));
		coyLabel.setForeground(fg);
		if (coyLogo != null)
			coyLabel.setIcon(new ImageIcon(coyLogo));
		if (coyName != null)
			coyLabel.setText(coyName);

		JLabel verLabel = new JLabel(version);
		verLabel.setFont(titleLabel.getFont().deriveFont(12F));
		verLabel.setForeground(fg);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(bg);
		bottomPanel.add(coyLabel, BorderLayout.EAST);
		bottomPanel.add(verLabel, BorderLayout.WEST);
		
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
	}
		
	/** Centers splash on screen and sets visible */
	public void showCenter()
	{
		//pack and display in middle of screen
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension splashSize = getPreferredSize();
		setLocation(screenSize.width/2 - (splashSize.width/2),
						screenSize.height/2 - (splashSize.height/2));
		setVisible(true);
	}

	
	/** Convenience method to create a splash screen. Remember you need to dispose of it when you're done starting */
	
	
	/**
	 * "Test harness" to run splash screen
	 */
	public static void main(String[] args)
	{
		new Splash("Test Splash","A demonstration splash screen", "v1.0",
					 Color.green, Color.white,
					 null, "MCH");
	}
	
}

