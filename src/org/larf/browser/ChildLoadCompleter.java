/**
 * $Id: ChildLoadCompleter.java,v 1.1 2005-04-01 01:54:56 mch Exp $
 *
 */

package org.larf.browser;

import org.larf.FileNode;



/**
 * Runnable that does stuff with the given children.  Used by child loader threads
 * once they have completed in order to update the display */

public abstract class ChildLoadCompleter implements Runnable {
      
      protected FileNode[] childFiles = null;
      
      public void setChildFiles(FileNode[] newChildFiles)  {
         this.childFiles = newChildFiles;
      }
      
   }

