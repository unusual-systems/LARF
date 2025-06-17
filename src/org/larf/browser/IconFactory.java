/*
 * $Id: IconFactory.java,v 1.5 2005-04-03 22:26:25 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 *
 */

package org.larf.browser;

import java.awt.Image;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * a cached factory of images stored off this package, for things such as
 * button icons etc
 *
 * @author Martin Hill
 *
 */


public class IconFactory extends ImageFactory
{
   public static final int SMALL = 0;   //for lines, status bars, etc
   public static final int MEDIUM = 1; //size suitable for toolbar buttons
   public static final int LARGE = 2;   //for dialog boxes, etc

   //pixel sizes for above
   private static final int[] PIXELS = new int[] { 16, 32, 64 };

   //cached icons - lookups for above
   private static final Hashtable[] cache = new Hashtable[] { new Hashtable(), new  Hashtable(), new Hashtable() };

   /** Get an icon suitable for message boxes, etc.  Looks first
    * for file, then for icons used by option pane in UI Manager
    */
   public static Icon getIcon(String iconName)
   {
      return getIcon(iconName, LARGE);
   }

   /** Get an icon suitable for display on lines, status bars, etc.
    * Looks first in hashtable, then for image file, then makes one
    * out of larger icon if necessary
    */
   public static Icon getSmallIcon(String iconName)
   {
      return getIcon(iconName, SMALL);
   }

   /** Get an icon with the size given
    * Looks first in hashtable, then for image file, then makes one
    * out of larger icon if necessary
    * @todo - this only deals with ImageIcons but it ought to not fail if there are other types
    */
   public static Icon getIcon(String iconName, int size)
   {
      assert (size >= SMALL) && (size <= LARGE) : "Size must be SMALL, MEDIUM or LARGE";
      
      //if it's in the hashtable, return that.
      ImageIcon icon = (ImageIcon) cache[size].get(iconName);
      if (icon != null)
         return icon;

      //if it's in the cache at this or a larger size, get that
      int trySize = size+1;
      while ((trySize<=LARGE) && (icon == null))
      {
         icon = (ImageIcon) cache[trySize].get(iconName);
         trySize++;
      }

      //if its not there, look for a file
      if (icon == null) {
         //look for file
         icon = loadIcon(iconName.trim());
      }

      //make sure correct size - shrink if not
      if (icon != null) {
         if (icon.getIconWidth() > PIXELS[size]) {
            icon = new ImageIcon(icon.getImage().getScaledInstance(PIXELS[size], PIXELS[size],Image.SCALE_REPLICATE));
         }
         cache[size].put(iconName, icon);
      }

      return icon;
   }

   /**
    * Loads Icon from file in the images subdirectory of this class
    */
   private static ImageIcon loadIcon(String filename)
   {
      Image image = loadImage(filename+".gif");
      if (image != null) {
         return new ImageIcon(image);
      }
      return null;
   }
}
/*
$Log: IconFactory.java,v $
Revision 1.5  2005-04-03 22:26:25  mch
Added images

Revision 1.4  2005/04/03 12:51:31  mch
split imagefactory from iconfactory


 */

