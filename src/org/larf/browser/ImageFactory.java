/*
 * $Id: ImageFactory.java,v 1.1 2005-04-03 12:51:31 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 *
 */

package org.larf.browser;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * a cached factory of images stored off this package, for things such as
 * button icons etc
 *
 * @author Martin Hill
 *
 */


public class ImageFactory
{
   //cached images
   private static final Hashtable imageCache = new Hashtable();

   private static final Logger logger = Logger.getLogger(ImageFactory.class.toString());
   
   /** Get an image suitable for message boxes, etc.  Does a typecast of getIcon()
    * for file, then for icons used by option pane in UI Manager
    */
   public static Image getImage(String imageName)
   {
      //if it's in the hashtable, return that.
      Image image = (Image) imageCache.get(imageName);
      if (image != null) {
         return image;
      }

      image = loadImage(imageName);
      
      if (image == null) {
         image = loadImage(imageName+".gif");
      }
      
      return image;
   }

   /**
    * Loads Image from file in the images subdirectory of this class
    */
   public static Image loadImage(String filename)
   {
      //path is subdirectory of this package, called images.  This doesn't work for JAR files
      String path = "./images/"+filename;
      URL url = IconFactory.class.getResource(path);
      logger.fine("Looking for image "+path+" got "+url);
      if (url != null) {
         return Toolkit.getDefaultToolkit().getImage(url);
      }

//      String path = "org/astrogrid/ui/images/"+filename+".gif";
      
      InputStream in = IconFactory.class.getResourceAsStream(path);
      logger.fine("getResourceAsStream returns "+in);
      if (in != null) {
         try {
            return ImageIO.read(in);
         }
         catch (IOException ioe) {
            //ignore, carry on looking some other way
         }
      }
      return null;
   }

   /**
    * Convenience routine for Loading Image from file in the images subdirectory of the given class
    */
   public static Image loadClassImage(Class givenClass, String filename)
   {
      String path = "./images/"+filename;

      //path is subdirectory of this package, called images
      URL url = givenClass.getResource(path);
      if (url != null) {
         return Toolkit.getDefaultToolkit().getImage(url);
      }

      InputStream in = givenClass.getResourceAsStream(path);
      logger.fine("getResourceAsStream returns "+in);
      if (in != null) {
         try {
            return ImageIO.read(in);
         }
         catch (IOException ioe) {
            //ignore, carry on looking some other way
         }
      }
      return null;
   }


}
/*
$Log: ImageFactory.java,v $
Revision 1.1  2005-04-03 12:51:31  mch
split imagefactory from iconfactory

 */

