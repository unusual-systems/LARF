/*
 * $Id: DeleteAction.java,v 1.2 2005-04-04 01:10:15 mch Exp $
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
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.larf.FileNode;
import org.larf.browser.IconFactory;
import org.larf.browser.PleaseWait;



/**
 * Action for deleting a file - see package documentation
 */

public class DeleteAction extends FileChangingAction {

   SelectedFileGetter getter = null;

   public final static String ACTION_COMMAND = "Delete";
   
   public DeleteAction(SelectedFileGetter selectedGetter) {
      this.getter = selectedGetter;
      
      //set icons etc
      putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
      putValue(AbstractAction.LONG_DESCRIPTION, "Delete currently selected file or folder");
      putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
      putValue(AbstractAction.NAME, "Delete");
      putValue(AbstractAction.SHORT_DESCRIPTION, "Deletes selected item");
      putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("Delete"));
   }
      
   /** Carry out action */
   public void actionPerformed(ActionEvent e) {
      FileNode file = getter.getSelectedFile();
      if (file == null) {
         JOptionPane.showMessageDialog((Component) e.getSource(), "No File Selected to Delete", "Delete failed", JOptionPane.ERROR_MESSAGE);
         return;
      }
      if (file.isFolder()) {
         int response = JOptionPane.showConfirmDialog((Component) e.getSource(), "Are you sure you want to delete folder "+file+" and all its contents?", "Confirm Delete Folder "+file.getName(), JOptionPane.ERROR_MESSAGE);
         if (response != JOptionPane.YES_OPTION) {
            return;
         }
      }
      PleaseWait pw = new PleaseWait("Deleting "+file);
      try {
         file.delete();
         //update relevent displays...
         fireFileChanged(new FileChangedEvent(file, false, true));
      }
      catch (IOException ioe) {
         JOptionPane.showMessageDialog((Component) e.getSource(), ioe.getMessage(), "Deleting "+file, JOptionPane.ERROR_MESSAGE);
      }
      pw.hide();
   }

}

