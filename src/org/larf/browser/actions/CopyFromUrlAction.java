/*
 * $Id: CopyFromUrlAction.java,v 1.1 2005-04-04 01:10:15 mch Exp $
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
 * Action for copying a file from a url to the filesystem - see package documentation
 */

public class CopyFromUrlAction extends FileChangingAction {

   public final static String ACTION_COMMAND = "UploadUrl";
   
   SelectedFileGetter getter = null;
   
   /** Pass in localChooser so that we can have a single chooser being used for
    * all local operations  */
   public CopyFromUrlAction(SelectedFileGetter selectedGetter) {
      this.getter = selectedGetter;

      //set icons etc
//      putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
      putValue(AbstractAction.LONG_DESCRIPTION, "Copies file from URL to currently selected file/folder");
      putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
      putValue(AbstractAction.NAME, "Import Url");
      putValue(AbstractAction.SHORT_DESCRIPTION, "Copies URL to selected folder");
      putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("World"));
   }
      
   /** Upload button pressed - copy file from disk to Myspace */
   public void actionPerformed(ActionEvent e) {

      //see if a filename has been entered
      FileNode targetFile = getter.getSelectedFile();
      if (targetFile == null)   {
         JOptionPane.showMessageDialog((Component) e.getSource(), "Set directory or filename target");
         return;
      }
      
      String urlEntry = JOptionPane.showInputDialog((Component) e.getSource(), "Enter URL to upload");
      
      if (urlEntry != null)
      {
         try {
            URL sourceUrl = new URL(urlEntry);
            
            //confirm overwrite
            if (targetFile.isFile())
            {
               int response = JOptionPane.showConfirmDialog((Component) e.getSource(), "Are you sure you want to overwrite '"+targetFile.getName()+"?" );
               if (response == JOptionPane.NO_OPTION) {
                  return;
               }
            }
            transfer((Component) e.getSource(), sourceUrl, targetFile);
            //update relevent displays...
            //needs to be done from piping threadfireFileChanged(new FileChangedEvent(target, false, true));

         } catch (MalformedURLException mue) {
            log.log(Level.SEVERE, "Invalid URL '"+urlEntry+"' ", mue);
            JOptionPane.showMessageDialog((Component) e.getSource(), mue+", url='"+urlEntry+"'", "StoreBrowser", JOptionPane.ERROR_MESSAGE);
         } catch (IOException ioe) {
            log.log(Level.SEVERE, ioe+" Uploading '"+urlEntry+"' to "+targetFile, ioe);
            JOptionPane.showMessageDialog((Component) e.getSource(), ioe+", uploading "+urlEntry+" to '"+targetFile+"'", "StoreBrowser", JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   /** At the moment does the copy through this machine - it should really get the
    * remote server to connect directly if possible, and monitor that */
   public void transfer(Component parent, URL source, FileNode target) {
      try {
         PleaseWait waitBox = new PleaseWait("Connecting to server");
         URLConnection sourceConnection = source.openConnection();
         if (target.isFolder()) {
            //use the source's filename
            String path = source.getPath();
            if (path.indexOf("/") >-1) {
               target = target.getChild(path.substring(path.lastIndexOf("/")+1));
            }
            else {
               target = target.getChild(source.getPath());
            }
         }
         OutputStream out = target.openOutputStream(sourceConnection.getContentType(), false);
         InputStream in = sourceConnection.getInputStream();
         waitBox.hide();
         log.info("Copying from "+source+" to "+target);
         PipeProgressMonitor monitor = new PipeProgressMonitor(in, parent, "Copy["+source+"]", "Copying "+source+" to "+target, source+" copied", (int) sourceConnection.getContentLength());
         Piper.spawnPipe(in, out, monitor, StreamPiper.DEFAULT_BLOCK_SIZE);
      }
      catch (IOException e) {
         log.log(Level.SEVERE, e+" copying '"+source+"' to '"+target+"'",e);
         JOptionPane.showMessageDialog(parent, e+" copying '"+target+"': "+e, "Copy Failure", JOptionPane.ERROR_MESSAGE);
      }
   }
}

