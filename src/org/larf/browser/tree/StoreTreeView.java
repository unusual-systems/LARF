/*
 * $Id: StoreTreeView.java,v 1.8 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser.tree;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.larf.FileNode;
import org.larf.browser.TransferableFile;
import org.larf.browser.actions.FileChangedEvent;
import org.larf.browser.actions.FileChangedHub;
import org.larf.browser.actions.FileChangedListener;
import org.larf.browser.actions.SelectedFileGetter;

/**
 * A Tree view compnonent for displaying and exploring store file trees
 *
 */

public class StoreTreeView extends JTree implements FileChangedListener, SelectedFileGetter, MouseListener, DropTargetListener {
   

   
   //the operator of this view, not (necessarily) the owner of the files
   Principal operator = null;

   Logger log = Logger.getLogger(StoreTreeView.class.toString());

   JPopupMenu filePopupMenu = null;
   JPopupMenu folderPopupMenu = null;
   
   DropTarget dropTarget = null;
   
   public StoreTreeView(Principal aUser) throws IOException {
      super(new DirectoryTreeModel(new StoresList(aUser)));
      
      ((StoresList) getModel().getRoot()).setModel( (DefaultTreeModel) getModel());
      
      this.operator = aUser;
      setShowsRootHandles(true);
      setRootVisible(false);
      
      setCellRenderer(new FileViewNodeRenderer());
      
      FileChangedHub.addFileChangedListener(this);
      
      filePopupMenu = new FilePopupMenu( this);
      folderPopupMenu = new FolderPopupMenu( this);
      
      addMouseListener(this);

      //setTransferHandler(new FileTransferHandler());
      
      dropTarget = new DropTarget(this, this);
   }
   
   /** Convenience routine for returning the selected StoreFile rather than tree node */
   public FileNode getSelectedFile()
   {
      if (getSelectionPath() == null)  {
         return null;
      } else {
         Object node = getSelectionPath().getLastPathComponent();
         if (node instanceof FileViewNode) {
            return ((FileViewNode) node).getFile();
         }
         return null;
      }
   }

   /** Redisplays the given node after the node has been modified- called when eg files are added/deleted */
   public void review(TreeNode node) {
      ((DefaultTreeModel) getModel()).reload(node);
   }

   /** Convenience routine - returns the currently selected node as a StoreFileNode */
   public FileViewNode getSelectedNode() {
      if (getSelectionPath() == null)  {
         return null;
      } else {
         Object node = getSelectionPath().getLastPathComponent();
         if (node instanceof FileViewNode) {
            return ((FileViewNode) node);
         }
         return null;
      }
   }
   
   /** Ask user for a new store and include it in the preferences */
   public void askNewStore() {
      String newStore = JOptionPane.showInputDialog(this, "Enter Store URI");
      
      if ((newStore != null) && (newStore.trim().length()>0)) {
         Preferences prefs = Preferences.userNodeForPackage(StoresList.class);
         
         StoresList stores  = (StoresList) getModel().getRoot();
         
         String existingStorePrefs = prefs.get(StoresList.USER_STORELIST_KEY, "");
         if ((existingStorePrefs != null) && (existingStorePrefs.trim().length()>0)) {
            existingStorePrefs = existingStorePrefs + ", ";
         }
         existingStorePrefs = existingStorePrefs + newStore;
         
         prefs.put(StoresList.USER_STORELIST_KEY, existingStorePrefs);
         
         try {
            stores.addStore(newStore, newStore);
         }
         catch (IOException ioe) {
            JOptionPane.showConfirmDialog(this, ioe+ " adding store "+newStore, "Adding Store", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
         }

         try {
            prefs.flush();
         }
         catch (BackingStoreException e) {
            JOptionPane.showMessageDialog(this, e+ " saving preferences ", "Adding Store", JOptionPane.ERROR_MESSAGE);
         }
      }
   }
   
   /** Ask user for a new store and include it in the preferences */
   public void removeStore(StoreRootNode store) {

      //remove from preferences...
      Preferences prefs = Preferences.userNodeForPackage(StoresList.class);
      
      String existingStorePrefs = prefs.get(StoresList.USER_STORELIST_KEY, "");
      log.fine("User Prefs Stores List:"+existingStorePrefs);
      
      int loc = existingStorePrefs.indexOf(store.getName());
      if (loc == -1) {
         log.log(Level.SEVERE, "Store "+store+" not found in preferences "+existingStorePrefs);
      }
      else {
         if (loc+1+store.getName().length()>=existingStorePrefs.length()) {//might be last one
            existingStorePrefs = existingStorePrefs.substring(0, loc);
         }
         else {
            existingStorePrefs = existingStorePrefs.substring(0, loc)+existingStorePrefs.substring(loc+1+store.getName().length());
         }
      }
      prefs.put(StoresList.USER_STORELIST_KEY, existingStorePrefs);
      
      ((StoresList) getModel().getRoot()).removeStore(store);
      ((DirectoryTreeModel) getModel()).reload((TreeNode) getModel().getRoot());

      try {
         prefs.flush();
      }
      catch (BackingStoreException e) {
         JOptionPane.showMessageDialog(this, e+ " saving preferences ", "Removing Store "+store, JOptionPane.ERROR_MESSAGE);
      }
   }
   /**
    * Called when a file has changed somewhere.  Finds related node and reloads
    * if necessary.  At the moment just updates the selected node...
    */
   public void fileChanged(FileChangedEvent event) {

      //find relevent node...
      FileNode changedFile = event.getChangedFile();
      FileViewNode node = getSelectedNode();

      try {
         log.fine(" Refreshing "+node);
         node.getFile().refresh(); //reload file
         node.refresh(); //reload node - which should also force model reload on different thread
//         review(node);
         /*
         if (event.isChildrenChanged()) {
            FileViewNode parent = ((FileViewNode) node.getParent());
            log.debug(" Refreshing "+parent);
            parent.refresh(); //reload file list from remote server
            review(parent);
         }
         else if (event.isPropertiesChanged()) {
            log.debug(" Refreshing "+node);
            node.getFile().refresh();
            review(node);
         }
          */
      }
      catch (IOException ioe) {
         log.log(Level.SEVERE, ioe+" Refreshing "+changedFile,ioe);
         JOptionPane.showMessageDialog(this, ioe+ " refreshing file "+changedFile, "Updating Display", JOptionPane.ERROR_MESSAGE);
      }
   }

   /**
    * Invoked when a mouse button has been pressed on a component.  If
    * appropriate displays a popupmenu
    */
   public void mousePressed(MouseEvent e) {
      /*
      if (e.getButton()==1) {
         TreePath mousePath = getPathForLocation(e.getX(), e.getY());
         if (mousePath != null) {
            setSelectionPath(mousePath);
            FileViewNode selectedNode = (FileViewNode) mousePath.getLastPathComponent();
            //StoreTreeView view = (StoreTreeView)e.getSource();
            FileTransferHandler fth = new FileTransferHandler(selectedNode.file);
            fth.exportAsDrag(this, e, TransferHandler.COPY);
         }
      }
       */
   }
   
   /**
    * Invoked when a mouse button has been released on a component- does nothing
    */
   public void mouseReleased(MouseEvent e) {
   }
   
   /**
    * Invoked when the mouse button has been clicked (pressed
    * and released) on a component- does nothing.
    */
   public void mouseClicked(MouseEvent e) {
      
      if (e.isPopupTrigger() || (e.getButton()==3)) { //popup trigger flag doesn't always seem to work...
         TreePath mousePath = getPathForLocation(e.getX(), e.getY());
         if (mousePath != null) {
            setSelectionPath(mousePath);
            FileViewNode selectedNode = (FileViewNode) mousePath.getLastPathComponent();
            if (selectedNode instanceof StoreRootNode) {
                  new StorePopupMenu(this, (StoreRootNode) selectedNode).show(this, e.getX(), e.getY());
            }
            else {
               if (selectedNode.getFile().isFolder()) {
                  folderPopupMenu.show(this, e.getX(), e.getY());
               }
               else {
                  filePopupMenu.show(this, e.getX(), e.getY());
               }
            }
         }
      }
      
   }
   
   /**
    * Invoked when the mouse exits a component- does nothing.
    */
   public void mouseExited(MouseEvent e) { }

   /**
    * Invoked when the mouse enters a component - does nothing.
    */
   public void mouseEntered(MouseEvent e) {
   }
   
   /**
    * Called if the user has modified
    * the current drop gesture.
    * <P>
    * @param dtde the <code>DropTargetDragEvent</code>
    */
   public void dropActionChanged(DropTargetDragEvent dtde) {
   }
   
   /**
    * Called while a drag operation is ongoing, when the mouse pointer enters
    * the operable part of the drop site for the <code>DropTarget</code>
    * registered with this listener.
    *
    * @param dtde the <code>DropTargetDragEvent</code>
    */
   public void dragEnter(DropTargetDragEvent dtde) {
   }
   
   /**
    * Called while a drag operation is ongoing, when the mouse pointer has
    * exited the operable part of the drop site for the
    * <code>DropTarget</code> registered with this listener.
    *
    * @param dte the <code>DropTargetEvent</code>
    */
   public void dragExit(DropTargetEvent dte) {
   }
   
   /**
    * Called when a drag operation is ongoing, while the mouse pointer is still
    * over the operable part of the drop site for the <code>DropTarget</code>
    * registered with this listener.
    *
    * @param dtde the <code>DropTargetDragEvent</code>
    */
   public void dragOver(DropTargetDragEvent dtde) {
      TreePath mousePath = getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
      if (mousePath != null) {
         setSelectionPath(mousePath);
      }
   }
   
   /**
    * Called when the drag operation has terminated with a drop on
    * the operable part of the drop site for the <code>DropTarget</code>
    * registered with this listener.
    * <p>
    * This method is responsible for undertaking
    * the transfer of the data associated with the
    * gesture. The <code>DropTargetDropEvent</code>
    * provides a means to obtain a <code>Transferable</code>
    * object that represents the data object(s) to
    * be transfered.<P>
    * From this method, the <code>DropTargetListener</code>
    * shall accept or reject the drop via the
    * acceptDrop(int dropAction) or rejectDrop() methods of the
    * <code>DropTargetDropEvent</code> parameter.
    * <P>
    * Subsequent to acceptDrop(), but not before,
    * <code>DropTargetDropEvent</code>'s getTransferable()
    * method may be invoked, and data transfer may be
    * performed via the returned <code>Transferable</code>'s
    * getTransferData() method.
    * <P>
    * At the completion of a drop, an implementation
    * of this method is required to signal the success/failure
    * of the drop by passing an appropriate
    * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
    * dropComplete(boolean success) method.
    * <P>
    * Note: The data transfer should be completed before the call  to the
    * <code>DropTargetDropEvent</code>'s dropComplete(boolean success) method.
    * After that, a call to the getTransferData() method of the
    * <code>Transferable</code> returned by
    * <code>DropTargetDropEvent.getTransferable()</code> is guaranteed to
    * succeed only if the data transfer is local; that is, only if
    * <code>DropTargetDropEvent.isLocalTransfer()</code> returns
    * <code>true</code>. Otherwise, the behavior of the call is
    * implementation-dependent.
    * <P>
    * @param dtde the <code>DropTargetDropEvent</code>
    */
   public void drop(DropTargetDropEvent dtde) {

      //let's see what we've got
      Transferable t = dtde.getTransferable();
      try {
         FileNode file = (FileNode) t.getTransferData(TransferableFile.FILE_FLAVOUR);
         dtde.acceptDrop(TransferHandler.COPY);
         log.fine("Would insert "+file);
      }
      catch (IOException ioe) {
         log.log(Level.SEVERE, ioe+" dropping "+t,ioe);
         JOptionPane.showMessageDialog(this, ioe+ " dropping "+t, "Drag and drop", JOptionPane.ERROR_MESSAGE);
      }
      catch (UnsupportedFlavorException ufe) {
         log.log(Level.SEVERE, ufe+" dropping "+t,ufe);
         dtde.rejectDrop();
      }
   }
   
}
