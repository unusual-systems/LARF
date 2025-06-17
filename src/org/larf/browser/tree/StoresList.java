/*
 * $Id: StoresList.java,v 1.6 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;


import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.slinger.Slinger;

/**
 * Special one-off root storenode for tree models, that represents the highest level node, containing
 * homespace, local disk space, etc
 *
 */

public class StoresList extends DefaultMutableTreeNode {
   
   public static final String PERMIT_LOCAL_ACCESS_KEY = Slinger.PERMIT_LOCAL_ACCESS_KEY;
   
   public static final String USER_STORELIST_KEY = "stores";

   Principal user = null;
   
   DefaultTreeModel model = null; //for updating as children loaded
   
   /** Default constructor includes some standard stores appropriately configured */
   public StoresList(Principal aUser) throws IOException {
      this.user = aUser;

      children = new Vector();
   }
   
   public void setModel(DefaultTreeModel givenModel) throws IOException {
      model = givenModel;
      
      //add homespace
      //addHomespace();

      //add local filespace if appropriate
      /*
      if (ConfigFactory.getCommonConfig().getBoolean(PERMIT_LOCAL_ACCESS_KEY, false)) {
         File[] localRoots = File.listRoots();
         for (int i = 0; i < localRoots.length; i++) {
            if (!localRoots[i].getPath().startsWith("A:")) { //leave off the floppy disk from the auto list as you get popup boxes
               addStore(new StoreRootNode(model, this, localRoots[i].getPath(), localRoots[i], user));
            }
         }
      }
      */
      
      //add configured stores
      Preferences prefs = Preferences.userNodeForPackage(StoresList.class);
      String storeslist = prefs.get(USER_STORELIST_KEY, "");
      StringTokenizer s = new StringTokenizer(storeslist, ",");
      while (s.hasMoreTokens()) {
         String storeUri = s.nextToken().trim();
         addStore(storeUri, storeUri);
      }
      
      /*
      try {
         //load user options
         ConfigFactory.getUserOptions("Stores");
      }
      catch (IOException e) {
         //hmmm I think this actually should return an error, but I don't want it
         //to stop the constructor...
         LogFactory.getLog(StoresList.class).error(e+" loading User Options",e);
      }
       */
   }
   
   /**
    * This is not a leaf
    */
   public boolean isLeaf() {
      return false;
   }
   
   /** Add a store - informs model */
   public void addStore(StoreRootNode storeNode) {
      children.add(storeNode);
      storeNode.connect();
      model.reload(this);
   }
   
   /** Convenience routine for Add a store - informs model */
   public void addStore(String name, String uri) throws IOException {
      addStore(new StoreRootNode(model, this, name, uri, user));
   }

   /** Remove store with given name */
   public void removeStore(String name) {
      Enumeration e = children.elements();
      while (e.hasMoreElements()) {
         StoreRootNode store = (StoreRootNode) e.nextElement();
         if (store.getName().equals(name)) {
            children.remove(store);
         }
      }
      throw new IllegalArgumentException("Store '"+name+"' not found");
   }
   
   /** Remove given store */
   public void removeStore(StoreRootNode delStore) {
      children.remove(delStore);
   }
   /** Returns a set of test/default stores.  This is mostly used for 'quick hack'
    * GUIs to have an initial set of stores.
    */
   public void addTestStores() throws IOException {
//      addStore("Twmbarlwm:8080", "msrl:http://twmbarlwm.star.le.ac.uk:8080/astrogrid-mySpace-SNAPSHOT/services/Manager");
//      addStore("Twmbarlwm:8888", "msrl:http://twmbarlwm.star.le.ac.uk:8888/astrogrid-mySpace-SNAPSHOT/services/Manager");
//      addStore("Zoomalooma", "msrl:http://zhumulangma.star.le.ac.uk:8080/astrogrid-mySpace-SNAPSHOT/services/Manager");
//      addStore("Katatjuta", "msrl:http://katatjuta.star.le.ac.uk:8080/astrogrid-mySpace-SNAPSHOT/services/Manager");
//      addStore("ROE FTP", "ftp://ftp.roe.ac.uk/pub/");
//      addStore("Ed FTP", "ftp://ftp.ed.ac.uk/pub/");
//      addStore("Mirror Service", "ftp://ftp.mirrorservice.org/");
//      addStore("iBiblio", "ftp://ftp.ibiblio.org/");
//      addStore("Demo SRB", JargonFileAdaptor.DEMO_SRB_URI);
   }
   
   /** Adds homespace to the list of stores.  If not signed in, includes 'Frogs' demo homespace */
   public void addHomespace() throws IOException {
      if ((user != null) && (!user.getName().toUpperCase().startsWith("ANONYMOUS"))) {
         String userName = user.getName();
         int atIdx = userName.indexOf("@");
         if (atIdx == -1) {
            throw new IllegalArgumentException("User name "+userName+" must be of the form <name>@<community>, eg mch@roe.ac.uk");
         }
         addStore("MySpace", "homespace:"+user.getName());
      }
      else {
         //default to frogs as an example
         addStore("Frogs MySpace", "homespace:frog@org.astrogrid.release");
//       ConfigFactory.getCommonConfig().setProperty("homespace.frog@org.astrogrid.release","ivo://org.astrogrid.release/myspace");
//       ConfigFactory.getCommonConfig().setProperty("ivorn.org.astrogrid.release/myspace","myspace:http://katatjuta.star.le.ac.uk:8080/astrogrid-mySpace-SNAPSHOT/services/Manager#frog");
      }
   }
   
}

