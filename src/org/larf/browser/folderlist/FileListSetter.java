/*
 * $Id: FileListSetter.java,v 1.2 2005-04-01 17:32:25 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.folderlist;

import org.larf.browser.ChildLoadCompleter;

/** Swing components are not threadsafe, so adding children/making other changes
 have to be done through the EventQueue.  This class adds the given StoreFile children
 * as StoreFileNodes to the
 * given parent StoreFileNode and updates the model */

public class FileListSetter extends ChildLoadCompleter implements Runnable {
   
   DirectoryListModel model;
   
   public FileListSetter(DirectoryListModel givenModel) {
      this.model = givenModel;
   }
   
   /** Modifies Swing components, so only run from SwingUtilities.invokeLater() */
   public void run() {
      
      model.setChildren(childFiles);
   }
   
}


