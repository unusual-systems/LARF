/*
 * $Id: StorePopupMenu.java,v 1.1 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;



/**
 * POpup menu for stores - create new for each store
 */

public class StorePopupMenu extends JPopupMenu {

   StoreRootNode store;
   StoreTreeView treeView;
   
   /** Initialise icons etc */
   public StorePopupMenu(StoreTreeView tree, StoreRootNode aStore) {

      this.store = aStore;
      this.treeView = tree;
      
      JMenuItem refresh = new JMenuItem("Reconnect");
      refresh.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                store.refresh();
            }
         }
      );
      add(refresh);

      JMenuItem delete = new JMenuItem("Remove");
      delete.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treeView.removeStore(store);
            }
         }
      );
      add(delete);
      
   }

}

