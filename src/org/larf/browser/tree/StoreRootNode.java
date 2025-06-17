/*
 * $Id: StoreRootNode.java,v 1.6 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.larf.StoreFileResolver;

/**
 * Represents the root of a store service (eg homespace, myspace, ftp, disk, etc) which
 * also corresponds to some concept of 'the store' and so may be displayed slightly
 * differently.
 *
 */

public class StoreRootNode extends FileViewNode {
   
   String name = null;
   String uri = null;
   
   boolean connected = false;
   
   public StoreRootNode(DefaultTreeModel model, StoresList root, String storeName, String storeUri, Principal aUser) throws IOException {
      super(model, root, null, aUser);
      this.name = storeName;
      this.uri = storeUri;
   }

   public StoreRootNode(DefaultTreeModel model, StoresList root, String storeName, File localFile, Principal aUser) throws IOException {
      super(model, root, new org.larf.adaptors.file.FileAdaptor(localFile), aUser);
      this.name = storeName;
      this.uri = localFile.toURL().toString();
   }

   /** Spawns a thread so that the display can work while we connect.  This is not
    * properly threadsafe - it's OK as the treview is written 'just now', ie make
    * sure you can't call refresh while calling this... */
   public synchronized void connect() {

      if (!connected) {
         setCompleteness(CONNECTING);
         connected = true;
         InitialConnector loading = new InitialConnector(this);
         Thread loadingThread = new Thread(loading);
         loadingThread.start();
      }
      
   }
   
   /** Connect if not connected */
   public void refresh() {
      if (!connected) {
         connect();
      }
      super.refresh();
   }
   
   /** Thread that connects and loads first children */
   public class InitialConnector implements Runnable {
      
      FileViewNode node = null;
      
      public InitialConnector(StoreRootNode givenNode) {
         node = givenNode;
      }
      
      public void run() {
         try {
            node.file = StoreFileResolver.resolveFile(new URI(uri), user);
            node.setCompleteness(FileViewNode.CONNECTED);
         }
         catch (URISyntaxException e) {
            setError(e+" resolving storefile at "+uri, e);
            connected = false;
         }
//         catch (IOException e) {
 //           setError(e+" resolving storefile at "+uri, e);
  //          connected = false;
   //      }
         catch (Throwable e) {
            setError(e+" resolving storefile at "+uri, e);
            connected = false;
         }
         SwingUtilities.invokeLater(new NodePropertyChanger(node, node.model));
      }
   }
   
      /** Simple class that makes the appropriate Swing component calls to update the
    * display as files come in.  Use SwingUtilities.invokeLater() to call it, so
    * that it's run from the GUI thread, as Swing components are not threadsafe */
   public class NodePropertyChanger implements Runnable {

      TreeNode node;
      DefaultTreeModel model;
      
      public NodePropertyChanger(TreeNode givenNode, DefaultTreeModel givenModel) {
         this.node = givenNode;
         this.model = givenModel;
         
         assert (node != null) : "Node is null";
         assert (model != null) : "Model is null";
      }
      
      public void run() {

         model.nodeChanged(node);
      }
      
   }
   
   public String getUri() {
      return uri;
   }
   
   public String getName() {
      return name;
   }

   /**
    * Returns true if the receiver is a leaf.
    */
   public boolean isLeaf() {
      return false;
   }
   
   
   /**
    * Returns true if the receiver allows children.
    */
   public boolean getAllowsChildren() {
      return true;
   }
   
   
}
