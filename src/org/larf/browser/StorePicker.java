/*
 * $Id: StorePicker.java,v 1.6 2005-04-04 01:10:15 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.larf.browser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.larf.FileNode;
import org.larf.browser.tree.FileViewNode;
import org.larf.browser.tree.StoreTreeView;
import org.larf.browser.tree.StoresList;
import org.slinger.account.IvoAccount;
import org.slinger.piper.Piper;

/**
 * A Dialog Box that provides a client view onto files in various forms of stores.  Uses storeclient
 * package so should work with anything that that package can handle.
 *
 * Intended to provide a modal JFileChooser style selector for choosing files.
 * Thus the features available are limited (saving screen real estate).  Also, construct
 * using the showDialog() so that the same instance of the picker is used each time,
 * thus preserving state from the last view.
 *
 * @author mch
 */


public class StorePicker extends JDialog
{
   JHistoryComboBox addressPicker = new JHistoryComboBox();

   StoreTreeView treeView = null;

   //action buttons & file selected - if present
   JButton actBtn = null;
   JButton cancelBtn = new JButton("Cancel");
   JTextField filenameField = new JTextField();
   JComboBox filetypePicker = new JComboBox();

   boolean isCancelled = false;

   //toolbar buttons
   JButton addStoreBtn = null;
   JButton refreshBtn = null;
   JButton deleteBtn = null;
   JButton newFolderBtn = null;

   //default size
   public static final int DEF_SIZE_X = 550;
   public static final int DEF_SIZE_Y = 350;
   
   protected static Logger log = Logger.getLogger(StorePicker.class.toString());
   
   //component operator
   Principal operator = null;

   /** Static instance, so when user asks for browser he gets it in same state as before */
   private static StorePicker picker = null;
   
   /** Public access to this picker - returns the storefile chosen (or null if cancelled).
    * @param action - text for action button, eg 'Open', 'Save', 'Get', etc.
    */
   public static FileNode choose(Principal user, String action) throws IOException
   {
      if (picker == null) {
         //initial constructor
         picker = new StorePicker(user, action);
         picker.setModal(true);
   
         picker.setSize(DEF_SIZE_X, DEF_SIZE_Y);

         ((StoresList) picker.treeView.getModel().getRoot()).addTestStores();
         picker.treeView.review((TreeNode) picker.treeView.getModel().getRoot());
      }

      picker.actBtn.setLabel(action);
      picker.show();

      if (picker.isCancelled) {
         return null;
      }
      return picker.getSelectedFile();
   }


   /** Constructor - private, use choose() */
   private StorePicker(Principal user, String action) throws IOException
   {
      super();
      this.operator = user;
      initComponents();
   }

   /**
    * Builds GUI and initialises components
    */
   private void initComponents() throws IOException
   {
      setTitle("Choosing File as "+operator.getName());
      
      addressPicker.addItem(""); //empty one to start with
      
      //toolbar
      addStoreBtn = IconButtonHelper.makeIconButton("AddStore", "AddStore", "Add store to the tree");
      refreshBtn = IconButtonHelper.makeIconButton("Refresh", "Refresh", "Reloads selected file/folder from server");
      newFolderBtn = IconButtonHelper.makeIconButton("New", "NewFolder", "Creates a new folder");
      deleteBtn = IconButtonHelper.makeIconButton("Del","Delete", "Deletes selected item");

      JPanel iconBtnPanel = new JPanel();
      BoxLayout btnLayout = new BoxLayout(iconBtnPanel, BoxLayout.X_AXIS);
      iconBtnPanel.setLayout(btnLayout);
      iconBtnPanel.add(addStoreBtn);
      iconBtnPanel.add(refreshBtn);
      iconBtnPanel.add(newFolderBtn);
      iconBtnPanel.add(deleteBtn);

      //action panel
      filetypePicker.addItem("All Files");
      actBtn = new JButton("OK");
      JPanel blPanel = new JPanel(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();
      GridBagHelper.setLabelConstraints(constraints);
      constraints.gridy++;
      blPanel.add(new JLabel("File Name: "), constraints);
      GridBagHelper.setEntryConstraints(constraints);
      blPanel.add(filenameField, constraints);
      constraints.gridy++;
      GridBagHelper.setLabelConstraints(constraints);
      blPanel.add(new JLabel("File Types: "), constraints);
      GridBagHelper.setEntryConstraints(constraints);
      blPanel.add(filetypePicker, constraints);

      JPanel actbtnPanel = new JPanel();
      actbtnPanel.add(actBtn);
      actbtnPanel.add(cancelBtn);
      
      JPanel actionPanel = new JPanel(new BorderLayout());
      actionPanel.add(actbtnPanel, BorderLayout.EAST);
      actionPanel.add(blPanel, BorderLayout.CENTER);

      //tree view
      treeView = new StoreTreeView(operator);
      JScrollPane treePanel = new JScrollPane();
      treePanel.getViewport().setView(treeView);

      //address pciker and toolbar combined
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.add(new JLabel("Address"), BorderLayout.WEST);
      topPanel.add(addressPicker, BorderLayout.CENTER);
      topPanel.add(iconBtnPanel, BorderLayout.EAST);

      topPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      actionPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(topPanel, BorderLayout.NORTH);
      getContentPane().add(treePanel, BorderLayout.CENTER);
      getContentPane().add(actionPanel, BorderLayout.SOUTH);
      
      addStoreBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               treeView.askNewStore();
            }
         }
      );
      
      refreshBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               refresh();
            }
         }
      );
      
      addressPicker.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//               treeView.setSelectionsetStore(addressPicker.getSelectedItem().toString());
            }
         }
      );
      
      cancelBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               isCancelled = true;
            }
         }
      );
      
      deleteBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               deleteSelected();
            }
         }
      );
      
      newFolderBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               newFolder();
            }
         }
      );
      
      actBtn.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               actBtnPressed();
            }
         }
      );

      treeView.addTreeSelectionListener(
         new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
               FileNode f = treeView.getSelectedFile();
   //might type in            boolean actable = false; //is what is chosen 'actable' - ie is  a file selected
               if (f!= null) {
                  addressPicker.addItem(f.getUri());
                  addressPicker.setSelectedItem(f.getUri());
                  if (f.isFile()) {
                     filenameField.setText(f.getName());
//                     actable = true;
                  }
               }
               else {
                  addressPicker.setSelectedItem(null);
               }
 //              actBtn.setEnabled(actable);
            }
         }
      );
      
      treeView.addMouseListener(
         new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               if (e.getClickCount()>1) {
                  if (treeView.getSelectedFile().isFile()) {
                     hide(); //item already selected on first click
                  }
               }
            }
         }
         );
      
      
      new EscEnterListener(this, actBtn, cancelBtn, true);
      
      isCancelled = false;
      
      pack();
      invalidate();
   }


   public void refresh() {
      FileViewNode node = treeView.getSelectedNode();
      TreePath path = treeView.getSelectionPath();
      
      if (node != null) {
         if (node.isLeaf()) {
            node = (FileViewNode) node.getParent(); //refresh folder rather than file
            path = path.getParentPath();
         }
         
         node.refresh();
         treeView.review(node);
      }
   }
   
   /**
    * Delete selected item
    */
   public void deleteSelected()
   {
      //see if a filename has been selected
      FileNode target = getSelectedFile();
      if (target == null)   {
         JOptionPane.showMessageDialog(this, "Select directory or filename to delete");
         return;
      }

      int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '"+target+"'?","Confirm Delete", JOptionPane.OK_CANCEL_OPTION);

//    if (response == JOptionPane.OK_OPTION) {
         //double check for non-empty folders?
//       if (target.isFolder() && target.
//    }
      
      if (response == JOptionPane.OK_OPTION) {
         try {
            target.getParent().refresh();
            target.delete();
            refresh();
         }
         catch (IOException ioe) {
            log.log(Level.SEVERE, ioe+", deleting "+target, ioe);
            JOptionPane.showMessageDialog(this, ioe+" deleting '"+target+"'", "StoreBrowser", JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   /**
    * Create new folder
    */
   public void newFolder()
   {
      //see if a filename has been selected
      FileNode target = getSelectedFile();
      if ((target == null) || (!target.isFolder()))   {
         JOptionPane.showMessageDialog(this, "Select directory to create new folder in");
         return;
      }
   
      String newFoldername = JOptionPane.showInputDialog(this, "Enter folder name to create");

      if (newFoldername != null) {
         //create folder
         try {
            target.makeFolder(newFoldername);
            refresh();
         } catch (IOException ioe) {
            log.log(Level.SEVERE, ioe+", creating new folder '"+newFoldername+"'", ioe);
            JOptionPane.showMessageDialog(this, ioe+", creating new folder '"+newFoldername+"'", "StoreBrowser", JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   
   /** Action button pressed - ie Open or Save */
   public void actBtnPressed()   {
      if (getSelectedFile() != null) {
         hide();
      }
      else {
         JOptionPane.showMessageDialog(this.getContentPane(), "Select a file to "+actBtn.getText());
      }
   }
   
   /** Returns the currently selected file, which may be selected on the tree view,
    * or may be selected via a folder on teh tree view and a name in the name field */
   public FileNode getSelectedFile() {
      FileNode file = treeView.getSelectedFile();
      
      if ((file != null) && (file.isFolder())) {
         if ((filenameField.getText() != null) && (filenameField.getText().trim().length()>0)) {
        	 try {
        		 file = file.getChild(filenameField.getText().trim());
        	 }
        	 catch (IOException ioe) {
                 log.log(Level.SEVERE, ioe+", looking for file '"+filenameField.getText().trim()+"'", ioe);
                 JOptionPane.showMessageDialog(this, ioe+", looking for file '"+filenameField.getText().trim()+"'", "StoreBrowser", JOptionPane.ERROR_MESSAGE);
        	 }
         }
      }
      
      return file;
   }
    
    /**
     * Standalone (for testing - for most standalone use see StoreBrowser).
     */
   public static void main(String[] args)  {

      try
      {
//         Principal user = new IvoAccount("DSATEST1", "uk.ac.le.star", null);
         Principal user = new IvoAccount("guest01", "uk.ac.le.star", null);
      if ((args.length>1)) {
         user = new IvoAccount(args[0]);
      }
         FileNode picked = StorePicker.choose(user, "Dump");
         System.out.println("Picked file "+picked+":");
         
         if ((picked != null) && (picked.isFile())) {
            Piper.bufferedPipe(picked.openInputStream(), System.out);
         }
         
      } catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
   }
}

/*
$Log: StorePicker.java,v $
Revision 1.6  2005-04-04 01:10:15  mch
proper actions, better (not perfect) threading, started drag drop

Revision 1.5  2005/04/01 10:41:02  mch
threading, preferences

Revision 1.4  2005/03/31 19:25:39  mch
semi fixed a few threading things, introduced sort order to tree

Revision 1.3  2005/03/29 20:13:51  mch
Got threading working safely at last

Revision 1.2  2005/03/28 03:28:49  mch
Some fixes for threadsafety

Revision 1.1  2005/03/28 02:06:35  mch
Major lump: split picker and browser and added threading to seperate UI interations from server interactions

 */

