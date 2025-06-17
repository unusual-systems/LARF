/*
 * $Id: ChildNodeSetter.java,v 1.2 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import java.security.Principal;
import java.util.logging.Logger;

import javax.swing.tree.DefaultTreeModel;

import org.larf.browser.ChildLoadCompleter;

/** Swing components are not threadsafe, so adding children/making other changes
 have to be done through the EventQueue.  This class adds the given StoreFile children
 * as StoreFileNodes to the
 * given parent StoreFileNode and updates the model */

public class ChildNodeSetter extends ChildLoadCompleter {
   
   FileViewNode parent;
   DefaultTreeModel model;
   Principal user;
   
   static Logger log = Logger.getLogger(ChildNodeSetter.class.toString());
   
   public ChildNodeSetter(FileViewNode givenParent, DefaultTreeModel givenModel, Principal givenUser) {
      this.parent = givenParent;
      this.model = givenModel;
      this.user = givenUser;
      
      assert (parent != null) : "Parent is null";
   }
   
   /** Modifies Swing components, so only run from SwingUtilities.invokeLater() */
   public void run() {
      
      log.finest("Loading children...");
      parent.removeAllChildren();
      
      if (childFiles != null) {

         parent.setChildren(childFiles);
      }
      parent.setCompleteness(1);
      log.finest("...children loaded");
      
      if (model != null) model.reload(parent);
   }
   
}


