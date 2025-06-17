/*
 * $Id: DownloadAction.java,v 1.1 2005-04-04 01:10:15 mch Exp $
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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.larf.FileNode;
import org.larf.browser.IconFactory;



/**
 * Action for downloading a file - see package documentation
 */

public class DownloadAction extends FileChangingAction {

   public final static String ACTION_COMMAND = "Download";
   
   SelectedFileGetter getter = null;
   JFileChooser chooser = null;
   
   /** Pass in localChooser so that we can have a single chooser being used for
    * all local operations  */
   public DownloadAction(JFileChooser localChooser, SelectedFileGetter selectedGetter) {
      this.getter = selectedGetter;
      this.chooser = localChooser;

      //set icons etc
//      putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
      putValue(AbstractAction.LONG_DESCRIPTION, "Download currently selected file to local hard disk");
      putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
      putValue(AbstractAction.NAME, "Download");
      putValue(AbstractAction.SHORT_DESCRIPTION, "Downloads selected item");
      putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("Import"));
   }
      
   /** Download button pressed - copy selected file from store to disk */
   public void actionPerformed(ActionEvent e) {
      //get selected store path
      FileNode source = getter.getSelectedFile();
      
      //check a path (not just a folder) selected
      if ((source == null) || (source.isFolder())) {
         JOptionPane.showMessageDialog( (Component) e.getSource(), "Select file to get");
         return;
      }

      //ask for local file location to svae it to - start with same name as source
      File chosen = chooser.getCurrentDirectory();
      if (chosen != null) {
         chosen = new File(chosen, source.getName());
         chooser.setSelectedFile(chosen);
      }
      
      int response = chooser.showSaveDialog( (Component) e.getSource() );
      
      if (response == chooser.APPROVE_OPTION) {
         
    	 FileNode target = new org.larf.adaptors.file.FileAdaptor(chooser.getSelectedFile());
         
         CopyAction.transfer( (Component) e.getSource(), source, target);
      }
   }

}

