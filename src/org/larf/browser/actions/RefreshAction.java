/*
 * $Id: RefreshAction.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.larf.FileNode;
import org.larf.browser.IconFactory;



/**
 * Action for refreshing a file view - see package documentation
 */

public class RefreshAction extends FileChangingAction {

   SelectedFileGetter getter = null;

   public final static String ACTION_COMMAND = "Refresh";
   
   public RefreshAction(SelectedFileGetter selectedGetter) {
      this.getter = selectedGetter;
      
      //set icons etc
//      putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
      putValue(AbstractAction.LONG_DESCRIPTION, "Reload currently selected file or folder");
      putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
      putValue(AbstractAction.NAME, "Refresh");
      putValue(AbstractAction.SHORT_DESCRIPTION, "Reloads selected item");
      putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("Refresh"));
   }
      
   /** Carry out action */
   public void actionPerformed(ActionEvent e) {
      FileNode file = getter.getSelectedFile();
      
      if (file == null) {
         JOptionPane.showMessageDialog((Component) e.getSource(), "No File Selected", "Refresh Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      //update relevent displays...
      fireFileChanged(new FileChangedEvent(file, true, true));
   }

}

