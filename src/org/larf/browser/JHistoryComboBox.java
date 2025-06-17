/*
 * $Id: JHistoryComboBox.java,v 1.1 2004-04-15 16:37:06 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 *
 */

package org.larf.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;

/**
 *
 * A history combo box that remembers previous input and allows you to pick
 * from a list
 *
 * @author M Hill
 */


public class JHistoryComboBox extends JComboBox
{
   public JHistoryComboBox()
   {
      super();
      setEditable(true);
      
      addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               updateList(getText());
            }
         }
      );
   }

   public void setDefaultList(String[] list)
   {
      for (int i=0; i<list.length; i++)
      {
         addItem(list[i]);
      }
   }

   /** Checks to see if the input is already in the list, and if not adds it
    */
   public void updateList(Object item)
   {
      for (int i=0;i<getItemCount();i++) {
         if (item.equals(getItemAt(i))) {
            return;
         }
      }

      addItem(item);
   }
   
   /**
    * Sets entry
    */
   public void setItem(Object newEntry)
   {
      updateList(newEntry);
      getEditor().setItem(newEntry);
   }
   
   public String getText()
   {
      return getEditor().getItem().toString();
   }
   
}

/*
$Log: JHistoryComboBox.java,v $
Revision 1.1  2004-04-15 16:37:06  mch
More tidying

Revision 1.1  2004/03/03 17:40:58  mch
Moved ui package

Revision 1.1  2004/02/17 16:04:06  mch
New Desktop GUI

Revision 1.1  2004/02/17 03:53:43  mch
Nughtily large number of fixes for demo

 */

