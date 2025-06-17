/*
 * $Id: NodeFileAdder.java,v 1.3 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultTreeModel;

import org.larf.FileNode;

/** Swing components are not threadsafe, so adding children/making other changes
 have to be done through the EventQueue.  This class adds the given StoreFile children
 * as StoreFileNodes to the
 * given parent StoreFileNode and updates the model */

public class NodeFileAdder implements Runnable {
   
   FileViewNode parent;
   FileNode[] children;
   DefaultTreeModel model;
   Principal user;
   
   protected static Logger log = Logger.getLogger(NodeFileAdder.class.toString());
   
   public NodeFileAdder(FileViewNode givenParent, FileNode[] givenChildren, DefaultTreeModel givenModel, Principal givenUser) {
      this.parent = givenParent;
      this.children = givenChildren;
      this.model = givenModel;
      this.user = givenUser;
      
      assert (parent != null) : "Parent is null";
   }
   
   /** Modifies Swing components, so only run from SwingUtilities.invokeLater() */
   public void run() {
      
      if (children != null) {
         
         for (int i = 0; i < children.length; i++) {
            //LogFactory.getLog(NodeFileAdder.class).debug("Adding "+children[i]+" to "+parent);
            
            try {
               FileViewNode childNode = new FileViewNode(model, parent, children[i], user);
               
               //bit of a botch - make sure child doesn't refer to parent before inserting
               childNode.setParent(null);
               
               //insert
               parent.add(childNode);
            }
            catch (IOException e) {
            	log.log(Level.SEVERE, "Creating child node for "+children[i],e);
            }
            
         }
      }
      if (model != null) model.reload(parent);
   }
   
}


