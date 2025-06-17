/**
 * $Id: ImageLoader.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 */

package org.larf.browser.imageview;

import java.awt.Color;
import java.awt.Image;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.larf.FileNode;


/**
 * Runnable that loads the contents from the remote file, then calls SwingUtilities
 * with the given ContentSetter Runnable to do what it has to do to update the display */

public class ImageLoader implements Runnable {
   
	Logger log = Logger.getLogger(ImageLoader.class.toString());
   
   FileNode source;
   ImageContentsView target;
   
   public ImageLoader(FileNode imageFile, ImageContentsView targetDisplay) {
      this.source = imageFile;
      this.target = targetDisplay;
   }
   
   public void run() {
      try {
         log.finest("Reading contents of "+source.getPath()+"...");
         target.getStatusBar().setText("Please wait, connecting...");
         InputStream in = source.openInputStream();

         target.getStatusBar().setText("Please wait, reading...");
         Image image = ImageIO.read(in);
         target.getImageArea().setIcon(new ImageIcon(image));
         in.close();
         target.getStatusBar().setText("");
      }
      catch (Throwable ioe) {
         log.log(Level.SEVERE, ioe+" loading from "+source,ioe);
         target.getStatusBar().setText(ioe+" loading from "+source);
         target.getStatusBar().setForeground(Color.RED);
      }
   }

   /** Call to abort the update */
   public void abort() {
      log.fine("Aborting load for "+source+" (No way to abort image loading threads just now)");
   }
   
}

