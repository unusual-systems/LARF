/*
 * $Id: CopyAction.java,v 1.1 2005-04-04 01:10:15 mch Exp $
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.larf.FileNode;
import org.larf.browser.IconFactory;
import org.larf.browser.PipeProgressMonitor;
import org.larf.browser.PleaseWait;
import org.slinger.piper.Piper;
import org.slinger.piper.StreamPiper;



/**
 * Action for deleting a file - see package documentation
 */

public class CopyAction extends FileChangingAction {

   public final static String ACTION_COMMAND = "Copy";
   
   SelectedFileGetter sourceGetter = null;
   SelectedFileGetter targetGetter = null;
   
   public CopyAction(SelectedFileGetter selectedSourceGetter, SelectedFileGetter selectedTargetGetter) {
      this.sourceGetter = selectedSourceGetter;
      this.targetGetter= selectedTargetGetter;

      //set icons etc
//      putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
      putValue(AbstractAction.LONG_DESCRIPTION, "Copy file or folder");
      putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
      putValue(AbstractAction.NAME, "Copy");
      putValue(AbstractAction.SHORT_DESCRIPTION, "Copies file or folder");
      putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("Copy"));
   }
      
   /** Download button pressed - copy selected file from store to disk */
   public void actionPerformed(ActionEvent e) {
      //get selected store path
      FileNode source = sourceGetter.getSelectedFile();
      FileNode target = targetGetter.getSelectedFile();
      
      //check a path (not just a folder) selected
      if ((source == null) || (source.isFolder())) {
         JOptionPane.showMessageDialog( (Component) e.getSource(), "Select file to copy from");
         return;
      }

      //ask for local file location to svae it to
      transfer( (Component) e.getSource(), source, target);
      //update relevent displays...
//needs to be done in thread      fireFileChanged(new FileChangedEvent(source, false, true));
      //fireFileChanged(new FileChangedEvent(target, false, true));
   }

   /** Starts a Transfer from point to point. This needs to be separately threaded (so that the UI 
    * carries on running) and it also needs to have a separate instance of the server client, so that 
    * we don't end up with locks/interrupts between the upload and the exploring */
   //@todo
   public static void transfer(Component parent, FileNode source, FileNode target) {

      PleaseWait waitBox = new PleaseWait("Connecting to server");
      try {
         OutputStream out = target.openOutputStream(source.getMimeType(), false);
         InputStream in = source.openInputStream();
         waitBox.hide();
         waitBox = null;
         log.info("Copying from "+source+" to "+target);
         PipeProgressMonitor monitor = new PipeProgressMonitor(in, parent, "Copy["+source.getName()+"]", "Copying "+source+" to "+target, source+" copied", (int) source.getSize());
         Piper.spawnPipe(in, out, monitor, StreamPiper.DEFAULT_BLOCK_SIZE);
      }
      catch (IOException e) {
         log.log(Level.SEVERE, e+" copying '"+source+"' to '"+target+"'",e);
         JOptionPane.showMessageDialog(parent, e+" copying to '"+target+"'", "Copy Failure", JOptionPane.ERROR_MESSAGE);
         if (waitBox != null) {
            waitBox.hide();
         }
      }
   }
}

