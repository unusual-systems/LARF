/*
 * $Id: NewFolderAction.java,v 1.1 2005-04-04 01:10:15 mch Exp $
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
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.larf.FileNode;
import org.larf.browser.IconFactory;
import org.larf.browser.PleaseWait;



/**
 * Action for creating a new folder- see package documentation
 */

public class NewFolderAction extends FileChangingAction {

   SelectedFileGetter getter = null;

   public final static String ACTION_COMMAND = "NewFolder";
   
   public NewFolderAction(SelectedFileGetter selectedGetter) {
      this.getter = selectedGetter;
      
      //set icons etc
//      putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
      putValue(AbstractAction.LONG_DESCRIPTION, "Create a new folder");
      putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
      putValue(AbstractAction.NAME, "New Folder");
      putValue(AbstractAction.SHORT_DESCRIPTION, "Create a new folder");
      putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("NewFolder"));
   }
      
   /**
    * Create new folder
    */
   public void actionPerformed(ActionEvent e) {
      //see if a filename has been selected
      FileNode target = getter.getSelectedFile();
      if ((target == null) || (!target.isFolder()))   {
         JOptionPane.showMessageDialog((Component) e.getSource(), "Select directory to create new folder in");
         return;
      }
   
      String newFoldername = JOptionPane.showInputDialog((Component) e.getSource(), "Enter folder name to create");

      if (newFoldername != null) {
         //create folder
         PleaseWait pw = new PleaseWait("Creating "+newFoldername);
         try {
            target.makeFolder(newFoldername);
            //update relevent displays...
            fireFileChanged(new FileChangedEvent(target, false, true));
         } catch (IOException ioe) {
            log.log(Level.SEVERE, ioe+", creating new folder '"+newFoldername+"'", ioe);
            JOptionPane.showMessageDialog((Component) e.getSource(), ioe+", creating new folder '"+newFoldername+"'", "StoreBrowser", JOptionPane.ERROR_MESSAGE);
         }
         pw.hide();
      }
   }

}

