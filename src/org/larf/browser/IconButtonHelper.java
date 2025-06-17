/*
 * $Id: IconButtonHelper.java,v 1.1 2004/04/15 16:37:06 mch Exp $
 *
 * (C) Copyright Astrogrid...
 */

package org.larf.browser;


/**
 * Some convenience routines for making and arranging icon buttons
 *
 * @author M Hill
 */

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class IconButtonHelper
{
   /** Helper conveience method for making the little icon buttons on top right */
   public  static JButton makeIconButton(String title)
   {
      return makeIconButton(title, title, null);
   }

   /** Helper conveience method for making the little icon buttons on top right */
   public  static JButton makeIconButton(String title, String iconname, String tooltip)
   {
      JButton b = null;
      Icon i = IconFactory.getIcon(iconname, IconFactory.MEDIUM);
      if (i == null)
      {
         b = new JButton(title);
      }
      else
      {
         b = new JButton(i);
         b.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
         int size = Math.max(i.getIconWidth(), i.getIconHeight());
         b.setPreferredSize(new Dimension(size+2, size+2));
      }
      
      b.setToolTipText(tooltip);
      return b;
   }

   /** Runs through making sure they are all the (height) of the largest, and
    * non-text ones are made the same width
    * */
   public static void sizeButtons(JButton[] buttons)
   {
      //find largest size
      //NB often at creation time the sizes are not set
      int size = 0;
      for (int b=0;b<buttons.length;b++)
      {
         if (buttons[b].getIcon() != null) {
            size = Math.max(size, buttons[b].getIcon().getIconHeight());
         }
         if (buttons[b].getText() == null) {
            if (size<buttons[b].getWidth())  size = buttons[b].getWidth();
         }
      }

      for (int b=0;b<buttons.length;b++)
      {
         if (buttons[b].getText() == null) { //icon button
            buttons[b].setPreferredSize(new Dimension(size+2, size+2));
         } else { //text button - just set height
            buttons[b].setSize(buttons[b].getWidth(), size+2);
         }
      }
   }
   
}

/*
$Log: IconButtonHelper.java,v $
Revision 1.1  2004/04/15 16:37:06  mch
More tidying

Revision 1.1  2004/03/03 17:40:58  mch
Moved ui package

Revision 1.1  2004/02/17 16:04:06  mch
New Desktop GUI

Revision 1.1  2004/02/17 03:53:43  mch
Nughtily large number of fixes for demo

 */

