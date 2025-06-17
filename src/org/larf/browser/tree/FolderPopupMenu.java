/*
 * $Id: FolderPopupMenu.java,v 1.2 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.larf.browser.actions.CopyFromUrlAction;
import org.larf.browser.actions.DeleteAction;
import org.larf.browser.actions.RefreshAction;
import org.larf.browser.actions.SelectedFileGetter;
import org.larf.browser.actions.UploadAction;



/**
 * popup menu for folders
 */

public class FolderPopupMenu extends JPopupMenu {

   /** Creeate menu  */
   public FolderPopupMenu(final SelectedFileGetter getter) {

      add(new JMenuItem(new RefreshAction(getter)));

      add(new JMenuItem(new DeleteAction(getter)));
      
      add(new JMenuItem(new UploadAction(new JFileChooser(), getter)));

      add(new JMenuItem(new CopyFromUrlAction(getter)));
      
   }

}

