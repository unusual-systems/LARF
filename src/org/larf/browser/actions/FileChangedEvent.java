/**
 * $Id: FileChangedEvent.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 */

package org.larf.browser.actions;

import org.larf.FileNode;


/** Used when a file has been changed.    propertiesChanged=true if things like name, size, date, etc
    * have changed, and childrenChanged=true if the number of children have
    * changed */

public class FileChangedEvent
{
   FileNode file = null;
   boolean properties = false;
   boolean children = false;
   
   public FileChangedEvent(FileNode changedFile, boolean propertiesChanged, boolean childrenChanged) {
      file = changedFile;
      this.properties = propertiesChanged;
      this.children = childrenChanged;
   }
   
   public FileNode getChangedFile() {
      return file;
   }
   
   public boolean isPropertiesChanged() {
      return properties;
   }
   
   public boolean isChildrenChanged() {
      return children;
   }
   
}

