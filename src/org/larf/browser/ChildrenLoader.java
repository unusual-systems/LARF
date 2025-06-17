/**
 * $Id: ChildrenLoader.java,v 1.2 2005-04-04 01:10:15 mch Exp $
 *
 */

package org.larf.browser;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.larf.FileNode;


/**
 * Runnable that loads the children from the remote service, then calls SwingUtilities
 * with the given ChildrenLoaded Runnable to do what it has to do to update the display */

public class ChildrenLoader implements Runnable {
   
	Logger log = Logger.getLogger(ChildrenLoader.class.toString());
   
   boolean aborted = false;
   ChildLoadCompleter completer;
   FileNode directory;
   FileSorter sorter;
   
   public ChildrenLoader(FileNode aDirectory, ChildLoadCompleter aCompleter, FileSorter aSorter) {
      this.completer = aCompleter;
      this.directory = aDirectory;
      this.sorter = aSorter;
   }
   
   public void run() {
      try {
         log.fine("Reading file list for "+directory.getPath()+"...");
               //this is the bit that might take some time
               //leave this to the ueser node.getFile().refresh();
         //this is the bit that might take some time
         //don't do this for this display - it may already be done for the other model.directory.refresh();
         FileNode[] childFiles = directory.listFiles();
         if (childFiles != null) {
            log.fine("...found "+childFiles.length+" files for "+directory.getPath());
         }
         else {
            log.fine("...found no files for "+directory.getPath());
         }
         //so we have the results, now add them in to the model/node, but do so in the ui thread (whether
         //or not they are empty)
         if (!aborted) {
            //sort
            childFiles = sorter.sort(childFiles);
         }
         
         if (!aborted) {
            completer.setChildFiles(childFiles);
            SwingUtilities.invokeLater(completer);
         }
      }
      catch (Throwable e) {
         log.log(Level.SEVERE, e+" loading children of "+this, e);
      }
   }
   
   /** Call to abort the update */
   public void abort() {
      aborted = true;
      log.fine("Aborting load for "+directory);
   }
}

