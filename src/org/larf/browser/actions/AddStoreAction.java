/*
 * $Id: AddStoreAction.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.larf.browser.IconFactory;
import org.larf.browser.tree.StoreTreeView;



/**
 * Action for adding a new store - see package documentation
 */

public class AddStoreAction extends FileChangingAction {

   public final static String ACTION_COMMAND = "AddStore";
   
   StoreTreeView treeView;
   
   public AddStoreAction(StoreTreeView tree) {
      
      this.treeView = tree;

      //set icons etc
//      putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      putValue(AbstractAction.ACTION_COMMAND_KEY, ACTION_COMMAND);
      putValue(AbstractAction.LONG_DESCRIPTION, "Add a new store to the tree");
      putValue(AbstractAction.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
      putValue(AbstractAction.NAME, "Add Store");
      putValue(AbstractAction.SHORT_DESCRIPTION, "Add Store");
      putValue(AbstractAction.SMALL_ICON, IconFactory.getIcon("AddStore"));
   }
      
   /**
    * Create new folder
    */
   public void actionPerformed(ActionEvent e) {
      treeView.askNewStore();
   }

}

